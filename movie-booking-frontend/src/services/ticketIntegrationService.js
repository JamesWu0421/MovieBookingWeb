// src/services/ticketIntegrationService.js
// 修正版：支援後端的 imageUrl 欄位
import showTicketPricesService from './showTicketPriceService'
import ticketPackageService from './ticketPackageService'
import packageItemsService from './packageItemsService'

export default {
  /**
   * 根據場次 ID 取得所有可用的票種資訊
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

      console.log('票價資料:', ticketPrices)
      console.log('套票資料:', packages)
      console.log('內容物資料:', allItems)

      if (!ticketPrices || ticketPrices.length === 0) {
        return []
      }

      // 2. 建立 package 映射表
      const packageMap = {}
      packages.forEach(pkg => {
        packageMap[pkg.id] = pkg
      })

      // 3. 建立 items 映射表
      const itemsMap = this._buildItemsMap(allItems)
      
      console.log('items 映射表:', itemsMap)

      // 4. 整合資料
      const tickets = ticketPrices.map(priceData => {
        const packageId = priceData.ticketPackageId || priceData.ticket_package_id
        const pkg = packageMap[packageId]
        
        if (!pkg) {
          console.warn(`找不到 package_id: ${packageId}`)
          return null
        }

        const items = itemsMap[packageId] || []
        const packageCode = pkg.packageCode || pkg.package_code
        
        // 關鍵修正：支援多種可能的欄位名稱
        // 後端如果是 getImageUrl()，JSON 會是 imageUrl
        // 後端如果是 getPackageImage()，JSON 會是 packageImage
        const packageImage = pkg.imageUrl || pkg.image_url || pkg.packageImage || pkg.package_image
        
        console.log(`票種 ${packageId}: code="${packageCode}", image="${packageImage}"`)

        // 組合完整的票種資料
        return {
          id: priceData.id,
          packageId: pkg.id,
          name: pkg.packageName || pkg.package_name,
          code: packageCode,
          type: (pkg.packageType || pkg.package_type || '').replace(/ /g, '_'),
          
          price: priceData.finalPrice || priceData.final_price,
          basePrice: priceData.screenBasePrice || priceData.screen_base_price,
          adjustment: priceData.ticketAdjustment || priceData.ticket_adjustment,
          isEarlyBird: (priceData.isEarlyBird || priceData.is_early_bird) === 1,
          earlyBirdAdjustment: priceData.earlyBirdAdjustment || priceData.early_bird_adjustment,
          durationSurcharge: priceData.durationSurcharge || priceData.duration_surcharge,
          
          items: items,
          description: this.generateDescription(pkg, items),
          
          // 關鍵修復：取得圖片 URL
          image: this.getPackageImage(packageImage),
          
          isAvailable: true,
          category: this.determineCategory(pkg.packageType || pkg.package_type, (priceData.isEarlyBird || priceData.is_early_bird) === 1, items)
        }
      }).filter(ticket => ticket !== null)

      console.log('整合後的票種:', tickets)
      return tickets

    } catch (error) {
      console.error('獲取票種資訊失敗:', error)
      throw error
    }
  },

  /**
   * 簡化的圖片處理 - 支援完整 URL 和相對路徑
   */
  getPackageImage(imageUrl) {
    // 安全檢查
    if (!imageUrl || 
        imageUrl === 'null' || 
        imageUrl === 'undefined' ||
        typeof imageUrl !== 'string' ||
        imageUrl.trim() === '' ||
        imageUrl === 'http://') {
      console.log('無效的圖片 URL:', imageUrl)
      return null
    }

    console.log('處理圖片:', imageUrl)

    // 如果已經是完整 URL（http:// 或 https://），直接返回
    if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) {
      console.log('使用完整 URL:', imageUrl)
      return imageUrl
    }

    // 如果是相對路徑，組合完整 URL
    const API_BASE_URL = 'http://localhost:8080'
    const imagePath = imageUrl.startsWith('/') ? imageUrl : `/${imageUrl}`
    const fullUrl = `${API_BASE_URL}${imagePath}`
    
    console.log('組合完整 URL:', fullUrl)
    return fullUrl
  },

  /**
   * 建立 items 映射表
   */
    _buildItemsMap(allItemsRaw) {
    console.log("進入 _buildItemsMap，原始輸入:", allItemsRaw);

    let allItems = [];

    if (Array.isArray(allItemsRaw)) {
      allItems = allItemsRaw;
    } else if (allItemsRaw && typeof allItemsRaw === 'object') {
      allItems =
        allItemsRaw.items ||
        allItemsRaw.itemList ||
        allItemsRaw.item_list ||
        allItemsRaw.data ||
        allItemsRaw.content ||
        [];

      if (!Array.isArray(allItems)) {
        console.warn("allItemsRaw 不是陣列也不是 items/content 格式:", allItemsRaw);
        allItems = [];
      }
    } else {
      console.warn("allItemsRaw 不是物件也不是陣列:", allItemsRaw);
      allItems = [];
    }

    console.log("整理後的 allItems:", allItems);

    const itemsMap = {};

    allItems.forEach(item => {
      const packageId = item.packageId || item.package_id;

      if (!packageId) {
        console.warn("item 缺 packageId:", item);
        return;
      }

      if (!itemsMap[packageId]) {
        itemsMap[packageId] = [];
      }

      itemsMap[packageId].push({
        id: item.id,
        type: item.itemType || item.item_type,
        name: item.itemName || item.item_name,
        spec: item.itemSpec || item.item_spec,
        quantity: item.quantity,
        displayOrder: item.displayOrder || item.display_order
      });
    });

    Object.keys(itemsMap).forEach(key => {
      itemsMap[key].sort((a, b) => a.displayOrder - b.displayOrder);
    });

    return itemsMap;
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
   * 判斷票種類別
   */
  determineCategory(packageType, isEarlyBird, items = []) {
    const movieTickets = items.filter(item => item.type === 'ticket')
    const totalTicketCount = movieTickets.reduce((sum, item) => sum + item.quantity, 0)
    const hasNonTicketItems = items.some(item => item.type !== 'ticket')
    
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
  // 如果沒有 items，當成一張電影票
  if (!ticket.items || ticket.items.length === 0) {
    return 1
  }

  // 比較保險：type 用小寫判斷，抓到「看起來像電影票」的項目
  const movieTickets = ticket.items.filter(item => {
    const t = (item.type || '').toLowerCase()
    return (
      t === 'ticket' ||
      t === 'movie_ticket' ||
      t === 'movie' ||
      t.includes('ticket')   // 例如 MOVIE_TICKET、TICKET_xxx 都會被抓到
    )
  })

  // 如果找不到任何「電影票」類型，就預設整個 package 當 1 張
  if (movieTickets.length === 0) {
    return 1
  }

  // 把 movie 類的數量加總
  return movieTickets.reduce((sum, item) => {
    const qty = item.quantity != null ? item.quantity : 1
    return sum + qty
  }, 0)
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
