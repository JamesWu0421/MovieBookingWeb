// src/stores/ticketspackages.js
import { defineStore } from 'pinia'
import ticketIntegrationService from '../services/ticketIntegrationService'

export const useTicketsPackagesStore = defineStore('ticketspackages', {
  state: () => ({
    // 票種資料（從 API 載入）
    tickets: [],
    
    // 票種類別定義 - 只保留：全部票種、單一票種、套票方案
    ticketCategories: [
      { id: 'all', name: '全部票種' },
      { id: 'single', name: '單一票種' },
      { id: 'combo', name: '套票方案' }
    ],
    
    // 當前選中的類別
    selectedCategory: 'all',
    
    // 購物車
    cart: [],
    
    // 載入狀態
    loading: false,
    error: null
  }),

  getters: {
    /**
     * 根據類別篩選票種
     */
    getTicketsByCategory: (state) => (categoryId) => {
      if (categoryId === 'all') {
        return state.tickets.filter(ticket => ticket.isAvailable)
      }
      return state.tickets.filter(
        ticket => ticket.category === categoryId && ticket.isAvailable
      )
    },

    /**
     * 計算購物車總金額
     */
    totalAmount: (state) => {
      return state.cart.reduce((sum, item) => {
        return sum + (item.price * item.quantity)
      }, 0)
    },

    /**
     * 計算購物車中的電影票總數
     */
    totalMovieTickets: (state) => {
      return state.cart.reduce((sum, item) => {
        const ticketCount = ticketIntegrationService.getMovieTicketCount(item)
        return sum + (ticketCount * item.quantity)
      }, 0)
    },

    /**
     * 取得購物車中的票種摘要
     */
    cartSummary: (state) => {
      return state.cart.map(item => {
        const ticketCount = ticketIntegrationService.getMovieTicketCount(item)
        return {
          name: item.name,
          ticketCount: ticketCount * item.quantity,
          quantity: item.quantity,
          subtotal: item.price * item.quantity,
          items: item.items
        }
      })
    }
  },

  actions: {
    /**
     * 根據場次 ID 載入票種
     */
    async fetchTicketsByShowId(showId) {
      this.loading = true
      this.error = null

      try {
        const tickets = await ticketIntegrationService.getAvailableTicketsByShowId(showId)
        this.tickets = tickets
        
        console.log(`成功載入 ${tickets.length} 個票種`)
        return tickets

      } catch (error) {
        console.error('載入票種失敗:', error)
        this.error = '無法載入票種資訊'
        throw error

      } finally {
        this.loading = false
      }
    },

    /**
     * 設定當前類別
     */
    setCategory(categoryId) {
      this.selectedCategory = categoryId
    },

    /**
     * 加入購物車
     */
    addToCart(ticket, quantity = 1) {
      const existingItem = this.cart.find(item => item.id === ticket.id)

      if (existingItem) {
        // 更新數量
        existingItem.quantity += quantity
      } else {
        // 新增項目
        this.cart.push({
          ...ticket,
          quantity
        })
      }

      console.log('購物車已更新:', this.cart)
    },

    /**
     * 更新購物車項目數量
     */
    updateCartItemQuantity(ticketId, quantity) {
      const item = this.cart.find(item => item.id === ticketId)

      if (quantity === 0) {
        // 移除項目
        this.removeFromCart(ticketId)
      } else if (item) {
        // 更新數量
        item.quantity = quantity
      } else if (quantity > 0) {
        // 新增項目
        const ticket = this.tickets.find(t => t.id === ticketId)
        if (ticket) {
          this.addToCart(ticket, quantity)
        }
      }
    },

    /**
     * 從購物車移除項目
     */
    removeFromCart(ticketId) {
      const index = this.cart.findIndex(item => item.id === ticketId)
      if (index > -1) {
        this.cart.splice(index, 1)
      }
    },

    /**
     * 清空購物車
     */
    clearCart() {
      this.cart = []
    },

    /**
     * 取得購物車中的票種數量
     */
    getCartItemQuantity(ticketId) {
      const item = this.cart.find(item => item.id === ticketId)
      return item ? item.quantity : 0
    },

    /**
     * 驗證購物車（檢查票數限制等）
     */
    validateCart(maxTickets = 6) {
      const totalTickets = this.totalMovieTickets

      if (totalTickets > maxTickets) {
        return {
          valid: false,
          message: `單次購票最多 ${maxTickets} 張，目前已選 ${totalTickets} 張`
        }
      }

      if (totalTickets === 0) {
        return {
          valid: false,
          message: '請至少選擇一張票'
        }
      }

      return {
        valid: true,
        message: ''
      }
    },

    /**
     * 計算剩餘可選票數
     */
    getRemainingTickets(maxTickets = 6) {
      return maxTickets - this.totalMovieTickets
    },

    /**
     * 取得票種詳細資訊
     */
    getTicketById(ticketId) {
      return this.tickets.find(ticket => ticket.id === ticketId)
    }
  }
})





