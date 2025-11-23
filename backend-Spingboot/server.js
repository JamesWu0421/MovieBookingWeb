const express = require('express');
const cors = require('cors');
const sql = require('mssql');
const crypto = require('crypto'); // 用來產生 ticket_uuid（GUID）

const app = express();
app.use(cors());
app.use(express.json());

// ⚙️ SQL Server 連線設定（改成你的帳密）
const dbConfig = {
    user: 'sa',           // TODO: 換成你的帳號
    password: 'P@ssw0rd', // TODO: 換成你的密碼
    server: 'localhost',
    database: 'cinemaDB',
    options: {
        encrypt: false,
        trustServerCertificate: true
    }
};

// 建立全域連線池
let poolPromise = sql.connect(dbConfig).then(pool => {
    console.log('✅ 已連線到 SQL Server');
    return pool;
}).catch(err => {
    console.error('❌ 資料庫連線失敗：', err);
});

// 工具：計算票券狀態
function getTicketStatus(showTime) {
    const now = new Date();
    const show = new Date(showTime);
    const expire = new Date(show.getTime() + 2 * 60 * 60 * 1000); // 開演後 2 小時

    if (now > expire) return 'EXPIRED';
    return 'VALID';
}

/**
 * API 1：建立票券 & 回傳票券資料 + qrcode_text
 * POST /api/tickets
 * body: { movieTitle, showTime, seat, screen }
 */
app.post('/api/tickets', async (req, res) => {
    try {
        const { movieTitle, showTime, seat, screen } = req.body;

        if (!movieTitle || !showTime) {
            return res.status(400).json({ message: 'movieTitle 和 showTime 為必填欄位' });
        }

        const pool = await poolPromise;

        // 產生唯一 ticket_uuid（GUID）
        const ticketUuid = crypto.randomUUID(); // 例如：'550e8400-e29b-41d4-a716-446655440000'
        const qrcodeText = ticketUuid;          // QR 裡面就寫這組字串

        // 建立一筆「CREATE」紀錄
        await pool.request()
            .input('ticket_uuid', sql.UniqueIdentifier, ticketUuid)
            .input('qrcode_text', sql.NVarChar(255), qrcodeText)
            .input('movie_title', sql.NVarChar(100), movieTitle)
            .input('screen', sql.NVarChar(50), screen || null)
            .input('seat', sql.NVarChar(20), seat || null)
            .input('show_time', sql.DateTime2, new Date(showTime))
            .input('action_type', sql.NVarChar(20), 'CREATE')
            .input('result', sql.NVarChar(20), 'SUCCESS')
            .input('location', sql.NVarChar(100), null)
            .input('note', sql.NVarChar(255), '建立票券')
            .query(`
                INSERT INTO verify_logs
                (ticket_uuid, qrcode_text, movie_title, screen, seat, show_time,
                 action_type, result, location, note)
                VALUES
                (@ticket_uuid, @qrcode_text, @movie_title, @screen, @seat, @show_time,
                 @action_type, @result, @location, @note)
            `);

        return res.json({
            ticketUuid,
            qrcodeText,
            movieTitle,
            screen,
            seat,
            showTime,
            status: 'UNUSED'
        });
    } catch (err) {
        console.error('建立票券失敗：', err);
        return res.status(500).json({ message: '建立票券失敗', error: err.message });
    }
});

/**
 * API 2：查詢票券狀態（給前端使用者看的）
 * GET /api/tickets/:ticketUuid
 */
app.get('/api/tickets/:ticketUuid', async (req, res) => {
    try {
        const { ticketUuid } = req.params;
        const pool = await poolPromise;

        // 找最新一筆 CREATE 記錄
        const createResult = await pool.request()
            .input('ticket_uuid', sql.UniqueIdentifier, ticketUuid)
            .query(`
                SELECT TOP 1 *
                FROM verify_logs
                WHERE ticket_uuid = @ticket_uuid AND action_type = 'CREATE'
                ORDER BY created_at DESC
            `);

        if (createResult.recordset.length === 0) {
            return res.status(404).json({ message: '找不到票券' });
        }

        const ticket = createResult.recordset[0];

        // 查是否已經有成功的 SCAN
        const scanResult = await pool.request()
            .input('ticket_uuid', sql.UniqueIdentifier, ticketUuid)
            .query(`
                SELECT TOP 1 *
                FROM verify_logs
                WHERE ticket_uuid = @ticket_uuid 
                  AND action_type = 'SCAN'
                  AND result = 'SUCCESS'
                ORDER BY created_at DESC
            `);

        let status = getTicketStatus(ticket.show_time); // 'VALID' 或 'EXPIRED'

        if (scanResult.recordset.length > 0) {
            status = 'USED'; // 有成功掃描過
        }

        return res.json({
            ticketUuid: ticket.ticket_uuid,
            qrcodeText: ticket.qrcode_text,
            movieTitle: ticket.movie_title,
            screen: ticket.screen,
            seat: ticket.seat,
            showTime: ticket.show_time,
            status
        });
    } catch (err) {
        console.error('查詢票券失敗：', err);
        return res.status(500).json({ message: '查詢票券失敗', error: err.message });
    }
});

/**
 * API 3：驗票（現場驗票端使用）
 * POST /api/verify
 * body: { qrcodeText, location }
 * 規則：
 * 1. 找不到 -> 記錄 NOT_FOUND
 * 2. 超過開演 + 2 小時 -> 記錄 EXPIRED
 * 3. 已成功刷過 -> 記錄 REUSED
 * 4. 以上都沒有 -> 記錄 SUCCESS
 */
app.post('/api/verify', async (req, res) => {
    try {
        const { qrcodeText, location } = req.body;

        if (!qrcodeText) {
            return res.status(400).json({ message: 'qrcodeText 為必填' });
        }

        const pool = await poolPromise;

        // 先找到對應的票券（CREATE 記錄）
        const createResult = await pool.request()
            .input('qrcode_text', sql.NVarChar(255), qrcodeText)
            .query(`
                SELECT TOP 1 *
                FROM verify_logs
                WHERE qrcode_text = @qrcode_text AND action_type = 'CREATE'
                ORDER BY created_at DESC
            `);

        if (createResult.recordset.length === 0) {
            // 找不到票券，記錄 NOT_FOUND
            await pool.request()
                .input('ticket_uuid', sql.UniqueIdentifier, '00000000-0000-0000-0000-000000000000')
                .input('qrcode_text', sql.NVarChar(255), qrcodeText)
                .input('movie_title', sql.NVarChar(100), '未知票券')
                .input('screen', sql.NVarChar(50), null)
                .input('seat', sql.NVarChar(20), null)
                .input('show_time', sql.DateTime2, new Date())
                .input('action_type', sql.NVarChar(20), 'SCAN')
                .input('result', sql.NVarChar(20), 'NOT_FOUND')
                .input('location', sql.NVarChar(100), location || null)
                .input('note', sql.NVarChar(255), '票券不存在')
                .query(`
                    INSERT INTO verify_logs
                    (ticket_uuid, qrcode_text, movie_title, screen, seat, show_time,
                     action_type, result, location, note)
                    VALUES
                    (@ticket_uuid, @qrcode_text, @movie_title, @screen, @seat, @show_time,
                     @action_type, @result, @location, @note)
                `);

            return res.json({ status: 'NOT_FOUND', message: '票券不存在' });
        }

        const ticket = createResult.recordset[0];
        const ticketUuid = ticket.ticket_uuid;

        // 判斷是否過期
        const statusByTime = getTicketStatus(ticket.show_time);
        if (statusByTime === 'EXPIRED') {
            await pool.request()
                .input('ticket_uuid', sql.UniqueIdentifier, ticketUuid)
                .input('qrcode_text', sql.NVarChar(255), qrcodeText)
                .input('movie_title', sql.NVarChar(100), ticket.movie_title)
                .input('screen', sql.NVarChar(50), ticket.screen)
                .input('seat', sql.NVarChar(20), ticket.seat)
                .input('show_time', sql.DateTime2, ticket.show_time)
                .input('action_type', sql.NVarChar(20), 'SCAN')
                .input('result', sql.NVarChar(20), 'EXPIRED')
                .input('location', sql.NVarChar(100), location || null)
                .input('note', sql.NVarChar(255), '超過電影開演兩小時')
                .query(`
                    INSERT INTO verify_logs
                    (ticket_uuid, qrcode_text, movie_title, screen, seat, show_time,
                     action_type, result, location, note)
                    VALUES
                    (@ticket_uuid, @qrcode_text, @movie_title, @screen, @seat, @show_time,
                     @action_type, @result, @location, @note)
                `);

            return res.json({ status: 'EXPIRED', message: '票券已過期（超過開演兩小時）' });
        }

        // 檢查是否已經成功刷過
        const scanResult = await pool.request()
            .input('ticket_uuid', sql.UniqueIdentifier, ticketUuid)
            .query(`
                SELECT TOP 1 *
                FROM verify_logs
                WHERE ticket_uuid = @ticket_uuid
                  AND action_type = 'SCAN'
                  AND result = 'SUCCESS'
                ORDER BY created_at DESC
            `);

        if (scanResult.recordset.length > 0) {
            // 已被刷過，記錄 REUSED
            await pool.request()
                .input('ticket_uuid', sql.UniqueIdentifier, ticketUuid)
                .input('qrcode_text', sql.NVarChar(255), qrcodeText)
                .input('movie_title', sql.NVarChar(100), ticket.movie_title)
                .input('screen', sql.NVarChar(50), ticket.screen)
                .input('seat', sql.NVarChar(20), ticket.seat)
                .input('show_time', sql.DateTime2, ticket.show_time)
                .input('action_type', sql.NVarChar(20), 'SCAN')
                
                .input('result', sql.NVarChar(20), 'REUSED')
                .input('location', sql.NVarChar(100), location || null)
                .input('note', sql.NVarChar(255), '重複刷入')
                .query(`
                    INSERT INTO verify_logs
                    (ticket_uuid, qrcode_text, movie_title, screen, seat, show_time,
                     action_type, result, location, note)
                    VALUES
                    (@ticket_uuid, @qrcode_text, @movie_title, @screen, @seat, @show_time,
                     @action_type, @result, @location, @note)
                `);

            return res.json({ status: 'REUSED', message: '此票券已使用過（重複刷入）' });
        }

        // 正常通過驗票，記錄 SUCCESS
        await pool.request()
            .input('ticket_uuid', sql.UniqueIdentifier, ticketUuid)
            .input('qrcode_text', sql.NVarChar(255), qrcodeText)
            .input('movie_title', sql.NVarChar(100), ticket.movie_title)
            .input('screen', sql.NVarChar(50), ticket.screen)
            .input('seat', sql.NVarChar(20), ticket.seat)
            .input('show_time', sql.DateTime2, ticket.show_time)
            .input('action_type', sql.NVarChar(20), 'SCAN')
            .input('result', sql.NVarChar(20), 'SUCCESS')
            .input('location', sql.NVarChar(100), location || null)
            .input('note', sql.NVarChar(255), '驗票成功')
            .query(`
                INSERT INTO verify_logs
                (ticket_uuid, qrcode_text, movie_title, screen, seat, show_time,
                 action_type, result, location, note)
                VALUES
                (@ticket_uuid, @qrcode_text, @movie_title, @screen, @seat, @show_time,
                 @action_type, @result, @location, @note)
            `);

        return res.json({
            status: 'SUCCESS',
            message: '驗票成功',
            ticket: {
                movieTitle: ticket.movie_title,
                screen: ticket.screen,
                seat: ticket.seat,
                showTime: ticket.show_time
            }
        });
    } catch (err) {
        console.error('驗票失敗：', err);
        return res.status(500).json({ message: '驗票失敗', error: err.message });
    }
});

// 啟動伺服器
const PORT = 3000;
app.listen(PORT, () => {
    console.log("🚀 後端啟動：http://localhost:3000");
});