import axios from "axios";

const client = axios.create();

export default {
  searchArticles(query, from, size) {
    return client.get("/api/search", {
      params: {
        corpus: "tangshi",
        query: query,
        from: from,
        size: size
      }
    });
  },
  downloadArticles(query) {
    var a = document.createElement("a");
    a.href = `/api/export?corpus=tangshi&query=${query}&from=0&size=2147483647`;
    a.click();
  }
};
