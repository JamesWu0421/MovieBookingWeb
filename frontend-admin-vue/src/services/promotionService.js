import api from "./api";

export default {
  list(params) {
    return api.get("/events", { params });
  },
  get(id) {
    return api.get(`/events/${id}`);
  },
  create(data) {
    return api.post("/events", data);
  },
  update(id, data) {
    return api.put(`/events/${id}`, data);
  },
  delete(id) {
    return api.delete(`/events/${id}`);
  },
};
