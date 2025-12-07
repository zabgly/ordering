// store/modules/loading.js
const state = {
    isLoading: false, // 是否正在加载
};

const mutations = {
    SET_LOADING(state, isLoading) {
        state.isLoading = isLoading;
    },
};

const actions = {
    setLoading({ commit }, isLoading) {
        commit('SET_LOADING', isLoading);
    },
};

export default {
    namespaced: true,
    state,
    mutations,
    actions,
};
