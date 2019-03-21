import axios from "axios";

const client = axios.create();
let id = 1;

export default {
  delState(id) {
    return Promise.resolve(id);
  },
  storeState(title, query, filtered) {
    console.log("store state", title, query, filtered);
    return Promise.resolve({
      id: ++id,
      title: title,
      query: query,
      filtered: filtered.slice(0)
    });
  },
  fetchAllState() {
    console.log("fecth all state");
    return Promise.resolve([]);
  }
};
