import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useTicketsPackagesStore = defineStore('ticketspackages', () => {
  // 票種類別
  const ticketCategories = ref([
    { id: 'ticket', name: '套票', active: true },
    { id: 'combo', name: '電影票', active: false }
  ])

  // 套票選項
  const comboTickets = ref([
    {
      id: 'combo-hot-coffee',
      name: '熱咖啡套票',
      price: 350,
      image: '/images/hot-coffee-combo.jpg',
      description: '內含:電影票 ✕ 1, 熱經典拿鐵 ✕ 1',
      items: [
        { name: '電影票', quantity: 1 },
        { name: '熱經典拿鐵', quantity: 1 }
      ]
    },
    {
      id: 'combo-popcorn',
      name: '爆米花套票',
      price: 420,
      image: '/images/popcorn-combo.jpg',
      description: '內含:電影票 ✕ 1, 大杯可樂 ✕ 1, 中爆米花46oz ✕ 1',
      items: [
        { name: '電影票', quantity: 1 },
        { name: '大杯可樂', quantity: 1 },
        { name: '中爆米花46oz', quantity: 1 }
      ]
    },
    {
      id: 'combo-double-popcorn',
      name: '雙人爆米花套票',
      price: 780,
      image: '/images/double-popcorn-combo.jpg',
      description: '內含:電影票 ✕ 2, 大杯可樂 ✕ 2, 大爆米花85oz ✕ 1',
      items: [
        { name: '電影票', quantity: 2 },
        { name: '大杯可樂', quantity: 2 },
        { name: '大爆米花85oz', quantity: 1 }
      ]
    },
    {
      id: 'combo-ice-coffee',
      name: '冰咖啡套票',
      price: 350,
      image: '/images/ice-coffee-combo.jpg',
      description: '內含:電影票 ✕ 1, 冰經典拿鐵 ✕ 1',
      items: [
        { name: '電影票', quantity: 1 },
        { name: '冰經典拿鐵', quantity: 1 }
      ]
    }
  ])

  // 一般電影票
  const regularTickets = ref([
    {
      id: 'ticket-full',
      name: '全票',
      price: 330,
      description: '內含:電影票 ✕ 1'
    },
    {
      id: 'ticket-discount',
      name: '優待票',
      price: 300,
      description: '內含:電影票 ✕ 1'
    }
  ])

  // 團票 (已停用)
  // const groupTickets = ref([
  //   {
  //     id: 'group-10',
  //     name: '團體票(10張)',
  //     price: 2800,
  //     description: '內含:電影票 ✕ 10'
  //   }
  // ])

  // 特殊票種 (已停用)
  // const specialTickets = ref([
  //   {
  //     id: 'special-charity',
  //     name: '愛心票',
  //     price: 165,
  //     description: '內含:電影票 ✕ 1'
  //   },
  //   {
  //     id: 'special-online',
  //     name: '線上劃位票',
  //     price: 330,
  //     description: '內含:電影票 ✕ 1'
  //   }
  // ])

  // 當前選中的票種類別
  const selectedCategory = ref('ticket')

  // 購物車 - 已選擇的票種
  const cart = ref([])

  // Getters
  // 根據類別取得票種
  const getTicketsByCategory = computed(() => {
    return (category) => {
      switch (category) {
        case 'ticket':
          return comboTickets.value
        case 'combo':
          return regularTickets.value
        default:
          return []
      }
    }
  })

  // 計算總金額
  const totalAmount = computed(() => {
    return cart.value.reduce((sum, item) => {
      return sum + (item.price * item.quantity)
    }, 0)
  })

  // 計算總票數
  const totalTickets = computed(() => {
    return cart.value.reduce((sum, item) => {
      return sum + item.quantity
    }, 0)
  })

  // Actions
  // 新增票種到購物車
  const addToCart = (ticket, quantity = 1) => {
    const existingItem = cart.value.find(item => item.id === ticket.id)
    
    if (existingItem) {
      existingItem.quantity += quantity
    } else {
      cart.value.push({
        ...ticket,
        quantity: quantity
      })
    }
  }

  // 更新購物車中的票種數量
  const updateCartItemQuantity = (ticketId, quantity) => {
    const item = cart.value.find(item => item.id === ticketId)
    
    if (item) {
      if (quantity <= 0) {
        removeFromCart(ticketId)
      } else {
        item.quantity = quantity
      }
    }
  }

  // 從購物車移除票種
  const removeFromCart = (ticketId) => {
    const index = cart.value.findIndex(item => item.id === ticketId)
    if (index > -1) {
      cart.value.splice(index, 1)
    }
  }

  // 清空購物車
  const clearCart = () => {
    cart.value = []
  }

  // 切換票種類別
  const setCategory = (category) => {
    selectedCategory.value = category
  }

  return {
    // State
    ticketCategories,
    comboTickets,
    regularTickets,
    selectedCategory,
    cart,

    // Getters
    getTicketsByCategory,
    totalAmount,
    totalTickets,

    // Actions
    addToCart,
    updateCartItemQuantity,
    removeFromCart,
    clearCart,
    setCategory
  }
})
