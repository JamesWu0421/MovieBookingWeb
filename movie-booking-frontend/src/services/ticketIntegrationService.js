// src/services/ticketIntegrationService.js
// ✅ 完全自動版本 - 等後端加上 getPackageId() 後使用
import showTicketPricesService from './showTicketPriceService'
import ticketPackageService from './ticketPackageService'
import packageItemsService from './packageItemsService'

export default {
  /**
   * 根據場次 ID 取得所有可用的票種資訊
   * @param {number} showId - 場次 ID
   * @returns {Promise<Array>} 整合後的票種資料
   */
  async getTicketsByShowId(showId) {
    try {
      // 1. 並行取得所有資料
      const [pricesRes, packagesRes, itemsRes] = await Promise.all([
        showTicketPricesService.getByShowId(showId),
        ticketPackageService.list(),
        packageItemsService.list()
      ])

      const ticketPrices = pricesRes.data
      const packages = packagesRes.data
      const allItems = itemsRes.data

      console.log('📦 票價資料:', ticketPrices)
      console.log('📦 套票資料:', packages)
      console.log('📦 內容物資料:', allItems)

      if (!ticketPrices || ticketPrices.length === 0) {
        return []
      }

      // 2. 建立 package 映射表
      const packageMap = {}
      packages.forEach(pkg => {
        packageMap[pkg.id] = pkg
      })

      // 3. 建立 items 映射表（自動從 API 建立）
      const itemsMap = this._buildItemsMap(allItems)
      
      console.log('🗺️ items 映射表:', itemsMap)

      // 4. 整合資料
      const tickets = ticketPrices.map(priceData => {
        const packageId = priceData.ticketPackageId || priceData.ticket_package_id
        const pkg = packageMap[packageId]
        
        if (!pkg) {
          console.warn(`找不到 package_id: ${packageId}`)
          return null
        }

        // 從映射表取得 items
        const items = itemsMap[packageId] || []
        
        if (items.length === 0) {
          console.warn(`package ${packageId} (${pkg.packageName || pkg.package_name}) 沒有內容物`)
        }

        // 組合完整的票種資料
        return {
          id: priceData.id,
          packageId: pkg.id,
          name: pkg.packageName || pkg.package_name,
          code: pkg.packageCode || pkg.package_code,
          type: (pkg.packageType || pkg.package_type || '').replace(/ /g, '_'),
          
          price: priceData.finalPrice || priceData.final_price,
          basePrice: priceData.screenBasePrice || priceData.screen_base_price,
          adjustment: priceData.ticketAdjustment || priceData.ticket_adjustment,
          isEarlyBird: (priceData.isEarlyBird || priceData.is_early_bird) === 1,
          earlyBirdAdjustment: priceData.earlyBirdAdjustment || priceData.early_bird_adjustment,
          durationSurcharge: priceData.durationSurcharge || priceData.duration_surcharge,
          
          items: items,
          description: this.generateDescription(pkg, items),
          image: this.getPackageImage(pkg.packageCode || pkg.package_code),
          isAvailable: true,
          category: this.determineCategory(pkg.packageType || pkg.package_type, (priceData.isEarlyBird || priceData.is_early_bird) === 1, items)
        }
      }).filter(ticket => ticket !== null)

      console.log('✅ 整合後的票種:', tickets)
      return tickets

    } catch (error) {
      console.error('獲取票種資訊失敗:', error)
      throw error
    }
  },

  /**
   * 建立 items 映射表
   * ✅ 自動從 API 資料建立（前提：API 要包含 packageId）
   */
  _buildItemsMap(allItems) {
    const itemsMap = {}
    
    allItems.forEach(item => {
      // 支援 camelCase 和 snake_case
      const packageId = item.packageId || item.package_id
      
      // 如果沒有 packageId，跳過這個 item
      if (!packageId) {
        console.warn('⚠️ item 缺少 packageId:', item)
        return
      }
      
      // 初始化陣列
      if (!itemsMap[packageId]) {
        itemsMap[packageId] = []
      }
      
      // 加入 item
      itemsMap[packageId].push({
        id: item.id,
        type: item.itemType || item.item_type,
        name: item.itemName || item.item_name,
        spec: item.itemSpec || item.item_spec,
        quantity: item.quantity,
        displayOrder: item.displayOrder || item.display_order
      })
    })
    
    // 排序每個 package 的 items
    Object.keys(itemsMap).forEach(packageId => {
      itemsMap[packageId].sort((a, b) => a.displayOrder - b.displayOrder)
    })
    
    return itemsMap
  },

  /**
   * 根據場次 ID 取得可用的票種
   */
  async getAvailableTicketsByShowId(showId) {
    const tickets = await this.getTicketsByShowId(showId)
    return tickets.filter(ticket => ticket.isAvailable)
  },

  /**
   * 生成票種描述
   */
  generateDescription(pkg, items) {
    const packageType = (pkg.packageType || pkg.package_type || '').replace(/ /g, '_')
    const packageName = pkg.packageName || pkg.package_name
    
    if (packageType === 'single_ticket') {
      const ticketItem = items.find(item => item.type === 'ticket')
      if (ticketItem) {
        return ticketItem.spec ? `${ticketItem.name} (${ticketItem.spec})` : ticketItem.name
      }
      return '電影票'
    } else if (packageType === 'bundle_ticket') {
      const descriptions = items
        .sort((a, b) => a.displayOrder - b.displayOrder)
        .map(item => {
          const spec = item.spec ? `(${item.spec})` : ''
          return `${item.name}${spec} × ${item.quantity}`
        })
      return `內含：${descriptions.join('、')}`
    } else {
      return packageName
    }
  },

  /**
   * 根據套票代碼取得圖片
   */
  getPackageImage(packageCode) {
    const imageMap = {
      'Discount': '/images/tickets/discount.png',
      'fullprice': '/images/tickets/regular.png',
      'coffee': '/images/tickets/coffee.png',
      'early': '/images/tickets/early-bird.png'
    }
    return imageMap[packageCode] || null
  },

  /**
   * 判斷票種類別
   * 新邏輯：只區分「單一票種」和「套票方案」
   * - 只有一張電影票 → single (單一票種)
   * - 其他情況（多張票或包含其他商品） → combo (套票方案)
   */
  determineCategory(packageType, isEarlyBird, items = []) {
    // 計算電影票數量
    const movieTickets = items.filter(item => item.type === 'ticket')
    const totalTicketCount = movieTickets.reduce((sum, item) => sum + item.quantity, 0)
    
    // 檢查是否有非票類商品
    const hasNonTicketItems = items.some(item => item.type !== 'ticket')
    
    // 判斷邏輯：
    // 1. 只有一張電影票且沒有其他商品 → 單一票種
    // 2. 其他情況（多張票或有其他商品） → 套票方案
    if (totalTicketCount === 1 && !hasNonTicketItems) {
      return 'single'
    } else {
      return 'combo'
    }
  },

  /**
   * 計算票種包含的電影票數量
   */
  getMovieTicketCount(ticket) {
    if (!ticket.items) return 1
    
    const movieTickets = ticket.items.filter(item => item.type === 'ticket')
    return movieTickets.reduce((sum, item) => sum + item.quantity, 0)
  },

  /**
   * 格式化票種內容物顯示文字
   */
  formatItemsText(items) {
    if (!items || items.length === 0) return ''
    
    return items
      .sort((a, b) => a.displayOrder - b.displayOrder)
      .map(item => {
        const spec = item.spec ? ` ${item.spec}` : ''
        return `${item.name}${spec} × ${item.quantity}`
      })
      .join('、')
  }
}





















































