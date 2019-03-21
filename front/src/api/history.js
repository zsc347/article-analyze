import axios from "axios";

const client = axios.create();

export default {
  delState(id) {
    console.log("api del", id);
    return client.delete(`/api/history/${id}`).then(rsp => rsp.data);
  },
  storeState(title, query, filtered) {
    let s = {
      title: title,
      query: query,
      filtered: filtered.slice(0)
    };
    console.log("api store", s);
    return client.put("/api/history", s).then(rsp => rsp.data);
  },
  fetchAllState() {
    return client.get("/api/history").then(rsp => rsp.data);
  }
};
