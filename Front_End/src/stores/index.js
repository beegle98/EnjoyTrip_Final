// store/index.js
import { createStore } from 'vuex'

export default createStore({
  state: {
    posts: [],
    filters: {
      category: '',
      sort: 'latest'
    },
    searchTerm: ''
  },
  mutations: {
    setPosts(state, posts) {
      state.posts = posts
    },
    setFilters(state, filters) {
      state.filters = { ...state.filters, ...filters }
    },
    setSearchTerm(state, term) {
      state.searchTerm = term
    }
  },
  actions: {
    async fetchPosts({ commit, state }) {
      // API 호출 로직 구현
    },
    async searchPosts({ commit }, searchTerm) {
      // 검색 API 호출 로직 구현
    }
  }
})