import article from "../../api/article";

const state = {
  size: 50,
  from: 0,
  total: 0,
  keys: [],
  results: []
};

const getters = {};

const mutations = {
  setResult(state, result) {
    state.results = result.results;
    state.total = result.total;
    state.keys = result.keys;
  }
};

const actions = {
  searchResults({ commit }, keyword) {
    article
      .searchArticles(keyword, state.from, state.size)
      .then(response => commit("setResult", response.data))
      .catch(err => commit("setResult", []));
  },
  downloadResults({}, keyword) {
    article.downloadArticles(keyword);
  },
  pageReset() {
    state.from = 0;
  },
  pageDown() {
    state.from = Math.max(0, state.from - state.size);
  },
  pageUp() {
    state.from = Math.max(0, state.from + state.size);
  }
};

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations
};
