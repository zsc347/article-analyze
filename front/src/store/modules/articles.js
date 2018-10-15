import article from "../../api/article";

const state = {
  results: []
};

const getters = {};

const mutations = {
  setResults(state, results) {
    console.log("setting results", results);
    state.results = results;
  }
};

const actions = {
  searchResults({ commit }, keyword) {
    article
      .searchArticles(keyword)
      .then(results => commit("setResults", results))
      .catch(err => commit("serResults", []));
  }
};

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations
};
