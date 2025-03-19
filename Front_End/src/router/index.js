import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useAlertStore } from '@/stores/alert' // 알림을 위한 스토어
import HomeView from '@/views/HomeView.vue'
import CreatePlan from '@/components/plan/CreatePlan.vue'
import SelectDestination from '@/components/plan/SelectDestination.vue'
import ManagePlan from '@/components/plan/ManagePlan.vue'
import TravelPlanDetails from '@/components/plan/TravelPlanDetails.vue'
import Tmap from '@/components/map/Tmap.vue'
import OauthRedirect from '@/components/oauth/OauthRedirect.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/oauth/redirect',
      name: 'OauthRedirect',
      component: OauthRedirect,
    },
    {
      path: '/tmap',
      name: 'tmap',
      component: Tmap,

    },
    {
      path: '/user',
      name: 'user',
      component: () => import('@/views/TheUserView.vue'),
      children: [
        {
          path: 'login',
          name: 'user-login',
          component: () => import('@/components/users/UserLogin.vue'),
        },
        {
          path: 'join',
          name: 'user-join',
          component: () => import('@/components/users/UserRegister.vue'),
        },
        {
          path: 'mypage',
          name: 'user-mypage',
          component: () => import('@/components/users/UserMyPage.vue'),
        },
        // {
        //   path: "modify/:userid",
        //   name: "user-modify",
        //   component: () => import("@/components/users/UserModify.vue"),
        // },
      ],
    },
    {
      path: '/notice',
      name: 'notice',
      component: () => import('@/views/TheNoticeView.vue'),
      redirect: { name: 'notice-list' },
      meta: { requiresAuth: true },
      children: [
        {
          path: 'list',
          name: 'notice-list',
          component: () => import('@/components/notice/NoticeList.vue'),
        },
        {
          path: 'view/:noticeno',
          name: 'notice-view',
          component: () => import('@/components/notice/NoticeDetail.vue'),
        },
        {
          path: 'write',
          name: 'notice-write',
          component: () => import('@/components/notice/NoticeWrite.vue'),
        },
        {
          path: 'modify/:noticeno',
          name: 'notice-modify',
          component: () => import('@/components/notice/NoticeModify.vue'),
        },
      ],
    },
    {
      path: '/board',
      name: 'board',
      component: () => import('@/views/TheBoardView.vue'),
      redirect: { name: 'board-main' },
      meta: { requiresAuth: true },
      children: [
        {
          path: '/main',
          name: 'board-main',
          component: () => import('@/views/board/TheMainView.vue'),
        },
        {
          path: '/schedule-review',
          name: 'PlanReview',
          component: () => import('@/views/board/ThePlanReviewView.vue')
        },
      ],

    },
    {
      path: '/attractionSearch',
      name: 'attractionSearch',
      component: () => import('@/views/TheAttractionSearchView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/travelreview',
      name: 'travelReview',
      component: () => import('@/views/board/TheTravelReviewView.vue'),
      redirect: { name: 'travelReview-list' },
      meta: { requiresAuth: true },
      children: [
        {
          path: 'list',
          name: 'travelReview-list',
          component: () => import('@/components/travelreview/TravelReviewList.vue'),
        },
        {
          path: 'view/:travelReviewNo',
          name: 'travelReview-view',
          component: () => import('@/components/travelreview/TravelReviewDetail.vue'),
        },
        {
          path: 'write',
          name: 'travelReview-write',
          component: () => import('@/components/travelreview/TravelReviewWrite.vue'),
        },
        {
          path: 'modify/:travelReviewNo',
          name: 'travelReview-modify',
          component: () => import('@/components/travelreview/TravelReviewModify.vue'),
        },
      ],
    },
    {
      path: '/todos',
      name: 'todos',
      component: () => import('@/views/TheTodoView.vue'),
    },
    {
      path: '/plan',
      name: 'plan',
      component: CreatePlan,
      meta: { requiresAuth: true },
    },
    {
      path: '/plan/select-destination',
      name: 'select-destination',
      component: SelectDestination,
      meta: { requiresAuth: true },
    },
    {
      path: '/plan/manage',
      name: 'manage-plan',
      component: ManagePlan,
      meta: { requiresAuth: true },
    },
    {
      path: '/travel-plan-details',
      name: 'travel-plan-details',
      component: TravelPlanDetails,
      meta: { requiresAuth: true },
      props: (route) => ({
        selectedTransport: route.query.selectedTransport,
      }),
    },
  ],
})
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  const alertStore = useAlertStore()

  if (to.matched.some(record => record.meta.requiresAuth)) {
    if (!authStore.isAuthenticated) {
      alertStore.showAlert({
        message: '이 페이지에 접근하려면 로그인이 필요합니다.',
        type: 'warning',
        action: {
          text: '로그인',
          callback: () => {
            router.push({
              path: '/user/login',
              query: { redirect: to.fullPath }
            })
          }
        }
      })
      next(false) // 현재 네비게이션을 중단
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router
