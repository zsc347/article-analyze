import article from "../../api/article";

const state = {
  results: []
};

const getters = {};

const mutations = {
  setResult(state, result) {
    state.results = result.results;
    state.total = result.total
  }
};

const actions = {
  searchResults({ commit }, keyword) {
    article
      .searchArticles(keyword)
      .then(response => commit("setResult", response.data))
      .catch(err => commit("setResults", []));
  }
};

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations
};
