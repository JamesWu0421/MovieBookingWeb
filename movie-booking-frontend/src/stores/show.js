import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useShowStore = defineStore('show', () => {
  // 影城列表
  const cinemas = ref([
        { id: 2, name: '台北欣欣秀泰' },
  ])

  // 場次資料
  // 資料結構: { cinemaId, movieId, date, hall, hallType, startTime, endTime, ticketStatus }
  const showtimes = ref([
    // 台北欣欣秀泰 - 11/18 的場次
    {
      cinemaId: 2,
      movieId: '1', // 這裡先用字串,之後可以根據實際的 movieId 調整
      date: '2025-11-18',
      hall: '1館9廳',
      hallType: '2D 英語',
      startTime: '10:00',
      endTime: '11:53',
      ticketStatus: '早優'
    },
    {
      cinemaId: 2,
      movieId: '1',
      date: '2025-11-18',
      hall: '1館10廳',
      hallType: '2D 英語',
      startTime: '10:40',
      endTime: '12:33',
      ticketStatus: '早優'
    },
    {
      cinemaId: 2,
      movieId: '1',
      date: '2025-11-18',
      hall: '1館6廳LOVE',
      hallType: '情人英語',
      startTime: '11:20',
      endTime: '13:13',
      ticketStatus: '早優'
    },
    {
      cinemaId: 2,
      movieId: '1',
      date: '2025-11-18',
      hall: '1館9廳',
      hallType: '2D 英語',
      startTime: '12:20',
      endTime: '14:13',
      ticketStatus: ''
    },
    {
      cinemaId: 2,
      movieId: '1',
      date: '2025-11-18',
      hall: '1館10廳',
      hallType: '2D 英語',
      startTime: '13:00',
      endTime: '14:53',
      ticketStatus: ''
    },
    {
      cinemaId: 2,
      movieId: '1',
      date: '2025-11-18',
      hall: '1館6廳LOVE',
      hallType: '情人英語',
      startTime: '13:40',
      endTime: '15:33',
      ticketStatus: ''
    },
    {
      cinemaId: 2,
      movieId: '1',
      date: '2025-11-18',
      hall: '1館9廳',
      hallType: '2D 英語',
      startTime: '14:40',
      endTime: '16:33',
      ticketStatus: ''
    },
    {
      cinemaId: 2,
      movieId: '1',
      date: '2025-11-18',
      hall: '1館10廳',
      hallType: '2D 英語',
      startTime: '15:20',
      endTime: '17:13',
      ticketStatus: ''
    },
    {
      cinemaId: 2,
      movieId: '1',
      date: '2025-11-18',
      hall: '1館6廳LOVE',
      hallType: '情人英語',
      startTime: '16:00',
      endTime: '17:53',
      ticketStatus: ''
    },
    {
      cinemaId: 2,
      movieId: '1',
      date: '2025-11-18',
      hall: '1館9廳',
      hallType: '2D 英語',
      startTime: '17:00',
      endTime: '18:53',
      ticketStatus: ''
    },
    {
      cinemaId: 2,
      movieId: '1',
      date: '2025-11-18',
      hall: '1館10廳',
      hallType: '2D 英語',
      startTime: '17:40',
      endTime: '19:33',
      ticketStatus: ''
    },
    {
      cinemaId: 2,
      movieId: '1',
      date: '2025-11-18',
      hall: '1館6廳LOVE',
      hallType: '情人英語',
      startTime: '18:20',
      endTime: '20:13',
      ticketStatus: ''
    },
    {
      cinemaId: 2,
      movieId: '1',
      date: '2025-11-18',
      hall: '1館9廳',
      hallType: '2D 英語',
      startTime: '19:20',
      endTime: '21:13',
      ticketStatus: ''
    },
    {
      cinemaId: 2,
      movieId: '1',
      date: '2025-11-18',
      hall: '1館10廳',
      hallType: '2D 英語',
      startTime: '20:00',
      endTime: '21:53',
      ticketStatus: ''
    },
    {
      cinemaId: 2,
      movieId: '1',
      date: '2025-11-18',
      hall: '1館6廳LOVE',
      hallType: '情人英語',
      startTime: '20:40',
      endTime: '22:33',
      ticketStatus: ''
    },
    {
      cinemaId: 2,
      movieId: '1',
      date: '2025-11-18',
      hall: '1館9廳',
      hallType: '2D 英語',
      startTime: '21:40',
      endTime: '23:33',
      ticketStatus: ''
    },
    
    // 台北欣欣秀泰 - 11/16 的場次
    {
      cinemaId: 2,
      movieId: '1',
      date: '2025-11-16',
      hall: '6廳',
      hallType: '2D 英語',
      startTime: '10:30',
      endTime: '12:23',
      ticketStatus: '早優'
    },
    {
      cinemaId: 2,
      movieId: '1',
      date: '2025-11-16',
      hall: '12廳',
      hallType: '2D 英語',
      startTime: '11:20',
      endTime: '13:13',
      ticketStatus: '早優'
    },
    {
      cinemaId: 2,
      movieId: '1',
      date: '2025-11-16',
      hall: '6廳',
      hallType: '2D 英語',
      startTime: '12:50',
      endTime: '14:43',
      ticketStatus: ''
    },
    {
      cinemaId: 2,
      movieId: '1',
      date: '2025-11-16',
      hall: '2廳',
      hallType: '2D 英語',
      startTime: '13:40',
      endTime: '15:33',
      ticketStatus: ''
    },
    {
      cinemaId: 2,
      movieId: '1',
      date: '2025-11-16',
      hall: '6廳',
      hallType: '2D 英語',
      startTime: '15:10',
      endTime: '17:03',
      ticketStatus: ''
    },
    {
      cinemaId: 2,
      movieId: '1',
      date: '2025-11-16',
      hall: '12廳',
      hallType: '2D 英語',
      startTime: '16:00',
      endTime: '17:53',
      ticketStatus: ''
    },
    {
      cinemaId: 2,
      movieId: '1',
      date: '2025-11-16',
      hall: '6廳',
      hallType: '2D 英語',
      startTime: '17:30',
      endTime: '19:23',
      ticketStatus: ''
    },
    {
      cinemaId: 2,
      movieId: '1',
      date: '2025-11-16',
      hall: '12廳',
      hallType: '2D 英語',
      startTime: '18:20',
      endTime: '20:13',
      ticketStatus: ''
    },
    {
      cinemaId: 2,
      movieId: '1',
      date: '2025-11-16',
      hall: '6廳',
      hallType: '2D 英語',
      startTime: '19:50',
      endTime: '21:43',
      ticketStatus: ''
    },
    {
      cinemaId: 2,
      movieId: '1',
      date: '2025-11-16',
      hall: '12廳',
      hallType: '2D 英語',
      startTime: '20:40',
      endTime: '22:33',
      ticketStatus: ''
    },
    {
      cinemaId: 2,
      movieId: '1',
      date: '2025-11-16',
      hall: '10廳',
      hallType: '2D 英語',
      startTime: '21:10',
      endTime: '23:03',
      ticketStatus: ''
    },
    {
      cinemaId: 2,
      movieId: '1',
      date: '2025-11-16',
      hall: '2廳',
      hallType: '2D 英語',
      startTime: '21:40',
      endTime: '23:33',
      ticketStatus: ''
    }
  ])

  // 載入狀態
  const loading = ref(false)
  const error = ref(null)

  // Getters
  // 根據影城ID獲取影城資訊
  const getCinemaById = computed(() => {
    return (cinemaId) => cinemas.value.find(c => c.id === cinemaId)
  })

  // 根據條件篩選場次
  const getShowtimes = computed(() => {
    return (filters = {}) => {
      let result = showtimes.value

      if (filters.cinemaId) {
        result = result.filter(s => s.cinemaId === filters.cinemaId)
      }

      if (filters.movieId) {
        result = result.filter(s => s.movieId === filters.movieId)
      }

      if (filters.date) {
        result = result.filter(s => s.date === filters.date)
      }

      if (filters.hallType && filters.hallType !== '所有') {
        result = result.filter(s => s.hallType === filters.hallType)
      }

      return result
    }
  })

  // 獲取某個影城和日期的所有廳種
  const getHallTypes = computed(() => {
    return (cinemaId, movieId, date) => {
      const types = new Set(['所有'])
      
      showtimes.value
        .filter(s => 
          s.cinemaId === cinemaId && 
          s.movieId === movieId && 
          s.date === date
        )
        .forEach(s => types.add(s.hallType))
      
      return Array.from(types)
    }
  })

  // Actions
  // 從 API 取得場次資料（未來實作）
  const fetchShowtimes = async (cinemaId, movieId) => {
    loading.value = true
    error.value = null
    
    try {
      // 這裡之後可以串接真實 API
      // const response = await fetch(`/api/showtimes?cinema=${cinemaId}&movie=${movieId}`)
      // const data = await response.json()
      // showtimes.value = data
      
      // 目前使用假資料，所以直接返回
      return showtimes.value
    } catch (err) {
      error.value = err.message
      console.error('取得場次失敗:', err)
    } finally {
      loading.value = false
    }
  }

  // 新增場次
  const addShowtime = (showtime) => {
    showtimes.value.push(showtime)
  }

  // 更新場次
  const updateShowtime = (index, newShowtime) => {
    if (index >= 0 && index < showtimes.value.length) {
      showtimes.value[index] = { ...showtimes.value[index], ...newShowtime }
    }
  }

  // 刪除場次
  const removeShowtime = (index) => {
    if (index >= 0 && index < showtimes.value.length) {
      showtimes.value.splice(index, 1)
    }
  }

  // 清空所有場次
  const clearShowtimes = () => {
    showtimes.value = []
  }

  return {
    // State
    cinemas,
    showtimes,
    loading,
    error,

    // Getters
    getCinemaById,
    getShowtimes,
    getHallTypes,

    // Actions
    fetchShowtimes,
    addShowtime,
    updateShowtime,
    removeShowtime,
    clearShowtimes
  }
})
