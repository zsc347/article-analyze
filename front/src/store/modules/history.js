import history from "../../api/history";

const state = {
  states: []
};

const getters = {};

const mutations = {
  setStates(state, states) {
    state.states = states;
  }
};

const actions = {
  delState({ commit }, id) {
    history.delState(id).then(s => {
      console.log("delete: ", JSON.stringify(s));
      commit("setStates", state.states.filter(s => s.id !== id));
      console.log("delState:", state.states);
    });
  },
  addState({ commit }, { title, query, filtered }) {
    history.storeState(title, query, filtered).then(s => {
      let newStates = state.states.slice(0);
      newStates.push(s);
      newStates.sort((s1, s2) => s1.id - s2.id);
      commit("setStates", newStates);
      console.log("setStates:", JSON.stringify(state.states));
    });
  },
  fetchState({ commit }) {
    history.fetchAllState().then(states => {
      commit("setStates", states);
      console.log("fetchState:", JSON.stringify(state.states));
    });
  }
};

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations
};
