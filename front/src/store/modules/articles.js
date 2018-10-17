import article from "../../api/article";

const state = {
  results: []
};

const getters = {};

const mutations = {
  setResults(state, results) {
    state.results = results;
  }
};

const actions = {
  searchResults({ commit }, keyword) {
    article
      .searchArticles(keyword)
      .then(response => commit("setResults", response.data.results))
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
