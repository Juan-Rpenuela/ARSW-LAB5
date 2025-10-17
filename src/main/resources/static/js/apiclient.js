// API client using jQuery AJAX with Promises
// Provides methods for GET/POST/PUT/DELETE against /blueprints endpoints

window.apiclient = (function () {
  const base = "/blueprints";

  const ajax = (options) => $.ajax(options);

  const getAll = () => ajax({ url: base, type: "GET" });

  const getByAuthor = (author) => ajax({ url: `${base}/${encodeURIComponent(author)}`, type: "GET" });

  const getOne = (author, name) => ajax({ url: `${base}/${encodeURIComponent(author)}/${encodeURIComponent(name)}`, type: "GET" });

  const post = (bp) => ajax({ url: base, type: "POST", data: JSON.stringify(bp), contentType: "application/json" });

  const put = (bp) => ajax({ url: base, type: "PUT", data: JSON.stringify(bp), contentType: "application/json" });

  const del = (author, name) => ajax({ url: `${base}/${encodeURIComponent(author)}/${encodeURIComponent(name)}`, type: "DELETE" });

  return { getAll, getByAuthor, getOne, post, put, del };
})();
