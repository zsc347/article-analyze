import article from "../../api/article";

const state = {
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
      .searchArticles(keyword)
      .then(response => commit("setResult", response.data))
      .catch(err => commit("setResult", []));
  }
};

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations
};
