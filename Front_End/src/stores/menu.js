import { computed } from "vue";
import { defineStore, storeToRefs } from "pinia";
import { useAuthStore } from "@/stores/auth";

export const useMenuStore = defineStore("menuStore", () => {
  const authStore = useAuthStore();
  const { isAuthenticated } = storeToRefs(authStore);

  const menuList = computed(() => [
    { name: "회원가입", show: !isAuthenticated.value, routeName: "user-join" },
    { name: "로그인", show: !isAuthenticated.value, routeName: "user-login" },
    //{ name: "", show: isAuthenticated.value, routeName: "todos" },
    { name: "내정보", show: isAuthenticated.value, routeName: "user-mypage" },
    { name: "로그아웃", show: isAuthenticated.value, routeName: "user-logout" },
  ]);

  return {
    menuList,
  };
});