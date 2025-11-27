/**
 * 格式化日期時間，移除 ISO 格式中的 T（僅用於顯示）
 * @param {string} dateString - ISO 格式的日期時間字串
 * @returns {string} 格式化後的日期時間字串
 */
export function formatDateTime(dateString) {
    if (!dateString) return ''

    try {
        // 移除 T 和毫秒部分
        // "2024-11-01T00:00:00" -> "2024-11-01 00:00:00"
        return dateString.replace('T', ' ').split('.')[0]
    } catch (error) {
        console.error('日期格式化錯誤:', error)
        return dateString
    }
}

/**
 * 將顯示格式的日期轉回 ISO 格式（用於送給後端）
 * @param {string} dateString - 格式化後的日期時間字串
 * @returns {string} ISO 格式的日期時間字串
 */
export function toISOFormat(dateString) {
    if (!dateString) return ''

    try {
        // "2024-11-01 00:00:00" -> "2024-11-01T00:00:00"
        if (dateString.includes(' ') && !dateString.includes('T')) {
            return dateString.replace(' ', 'T')
        }
        return dateString
    } catch (error) {
        console.error('日期格式轉換錯誤:', error)
        return dateString
    }
}

/**
 * 批次格式化陣列中的日期欄位
 * @param {Array} dataArray - 資料陣列
 * @param {Array} dateFields - 需要格式化的日期欄位名稱
 * @returns {Array} 格式化後的資料陣列
 */
export function formatDateFields(dataArray, dateFields = ['startDate', 'endDate', 'createdAt', 'updatedAt']) {
    if (!Array.isArray(dataArray)) return dataArray

    return dataArray.map(item => {
        const formatted = { ...item }
        dateFields.forEach(field => {
            if (formatted[field]) {
                formatted[field] = formatDateTime(formatted[field])
            }
        })
        return formatted
    })
}