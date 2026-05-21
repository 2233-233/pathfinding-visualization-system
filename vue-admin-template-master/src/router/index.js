import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

/* Layout */
import Layout from '@/layout'

/**
 * Note: sub-menu only appear when route children.length >= 1
 * Detail see: https://panjiachen.github.io/vue-element-admin-site/guide/essentials/router-and-nav.html
 *
 * hidden: true                   if set true, item will not show in the sidebar(default is false)
 * alwaysShow: true               if set true, will always show the root menu
 *                                if not set alwaysShow, when item has more than one children route,
 *                                it will becomes nested mode, otherwise not show the root menu
 * redirect: noRedirect           if set noRedirect will no redirect in the breadcrumb
 * name:'router-name'             the name is used by <keep-alive> (must set!!!)
 * meta : {
    roles: ['admin','editor']    control the page roles (you can set multiple roles)
    title: 'title'               the name show in sidebar and breadcrumb (recommend set)
    icon: 'svg-name'/'el-icon-x' the icon show in the sidebar
    noCache: true                if set true, the page will no be cached(default is false)
    affix: true                  if set true, the tag will affix in the tags-view
    breadcrumb: false            if set false, the item will hidden in breadcrumb(default is true)
    activeMenu: '/example/list'  if set path, the sidebar will highlight the path you set
  }
 */

/**
 * constantRoutes
 * a base page that does not have permission requirements
 * all roles can be accessed
 */
export const constantRoutes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    component: () => import('@/views/login/index'),
    hidden: true
  },
  {
    path: '/register',
    component: () => import('@/views/register/index'),
    hidden: true
  },
  {
    path: '/404',
    component: () => import('@/views/404'),
    hidden: true
  },

  // ========== 学生端路由 ==========
  {
    path: '/visualization',
    component: Layout,
    redirect: '/visualization/index',
    meta: {
      roles: ['student'] // 管理员通过"用户界面"子路由访问
    },
    children: [
      {
        path: 'index',
        component: () => import('@/views/visualization/index'),
        name: 'Visualization',
        meta: {
          title: '算法可视化',
          icon: 'el-icon-video-play',
          roles: ['student']
        }
      }
    ]
  },
  {
    path: '/algorithm',
    component: Layout,
    redirect: '/algorithm',
    name: 'Algorithm',
    meta: {
      title: '算法学习',
      icon: 'el-icon-notebook-2',
      roles: ['student']
    },
    children: [
      {
        path: '',
        component: () => import('@/views/algorithm/learning'),
        name: 'AlgorithmLearning',
        meta: {
          title: '算法学习',
          icon: 'el-icon-notebook-2',
          roles: ['student']
        }
      }
    ]
  },
  // 题目详情页作为独立路由
  {
    path: '/algorithm/problem/:id',
    component: Layout,
    hidden: true,
    meta: {
      roles: ['student']
    },
    children: [
      {
        path: '',
        component: () => import('@/views/algorithm/problem/_id'),
        name: 'ProblemDetail',
        meta: {
          title: '题目学习记录',
          roles: ['student'],
          hidden: true
        }
      }
    ]
  },
  {
    path: '/experiment',
    component: Layout,
    redirect: '/experiment/records',
    name: 'Experiment',
    meta: {
      title: '实验记录',
      icon: 'el-icon-document',
      roles: ['student']
    },
    children: [
      {
        path: 'records',
        component: () => import('@/views/experiment/records'),
        name: 'ExperimentRecords',
        meta: {
          title: '实验记录',
          icon: 'el-icon-tickets',
          roles: ['student']
        }
      }
    ]
  },
  // 题目学习记录（独立路由，与/user平级，放在个人中心前面）
  {
    path: '/problem-records',
    component: Layout,
    redirect: '/problem-records/index',
    name: 'ProblemRecords',
    meta: {
      title: '题目学习记录',
      icon: 'el-icon-document',
      roles: ['student']
    },
    children: [
      {
        path: 'index',
        component: () => import('@/views/user/problemRecords'),
        name: 'ProblemRecordsIndex',
        meta: {
          title: '题目学习记录',
          icon: 'el-icon-document',
          roles: ['student']
        }
      }
    ]
  },
  {
    path: '/user',
    component: Layout,
    redirect: '/user/profile',
    name: 'UserCenter',
    meta: {
      title: '个人中心',
      icon: 'el-icon-user',
      roles: ['student']
    },
    children: [
      {
        path: 'profile',
        component: () => import('@/views/user/profile'),
        name: 'Profile',
        meta: {
          title: '个人中心',
          icon: 'el-icon-user',
          roles: ['student']
        }
      }
    ]
  },

  // ========== 管理员端路由 ==========
  {
    path: '/admin',
    component: Layout,
    redirect: '/admin/dashboard',
    meta: {
      roles: ['admin']
    },
    children: [
      {
        path: 'dashboard',
        component: () => import('@/views/admin/dashboard'),
        name: 'AdminDashboard',
        meta: {
          title: '管理仪表板',
          icon: 'dashboard',
          roles: ['admin']
        }
      }
    ]
  },
  {
    path: '/admin/users',
    component: Layout,
    redirect: '/admin/users/index',
    name: 'UserManagement',
    meta: {
      title: '用户管理',
      icon: 'el-icon-user',
      roles: ['admin']
    },
    children: [
      {
        path: 'index',
        component: () => import('@/views/admin/users/index'),
        name: 'UserList',
        meta: {
          title: '用户列表',
          icon: 'el-icon-user',
          roles: ['admin']
        }
      }
    ]
  },
  // 功能页面作为独立路由
  {
    path: '/admin/users/add',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '',
        component: () => import('@/views/admin/users/add'),
        name: 'AddUser',
        meta: {
          title: '添加用户',
          roles: ['admin']
        }
      }
    ]
  },
  {
    path: '/admin/users/edit',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '',
        component: () => import('@/views/admin/users/edit'),
        name: 'EditUser',
        meta: {
          title: '编辑用户',
          roles: ['admin']
        }
      }
    ]
  },
  {
    path: '/admin/users/detail',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '',
        component: () => import('@/views/admin/users/detail'),
        name: 'UserDetail',
        meta: {
          title: '用户详情',
          roles: ['admin']
        }
      }
    ]
  },
  {
    path: '/admin/problems',
    component: Layout,
    meta: {
      roles: ['admin']
    },
    children: [
      {
        path: 'index',
        component: () => import('@/views/admin/problems/index'),
        name: 'ProblemManagement',
        meta: {
          title: '题库管理',
          icon: 'el-icon-edit-outline',
          roles: ['admin']
        }
      }
    ]
  },
  // {
  //   path: '/admin/algorithms',
  //   component: Layout,
  //   meta: {
  //     roles: ['admin']
  //   },
  //   children: [
  //     {
  //       path: 'index',
  //       component: () => import('@/views/admin/algorithms/index'),
  //       name: 'AlgorithmManagement',
  //       meta: {
  //         title: '算法管理',
  //         icon: 'el-icon-cpu',
  //         roles: ['admin']
  //       }
  //     }
  //   ]
  // },
  {
    path: '/admin/experiments',
    component: Layout,
    meta: {
      roles: ['admin']
    },
    children: [
      {
        path: 'index',
        component: () => import('@/views/admin/experiments/index'),
        name: 'ExperimentAnalytics',
        meta: {
          title: '实验记录管理',
          icon: 'el-icon-data-analysis',
          roles: ['admin']
        }
      }
    ]
  },

  // 用户界面（将学生端页面聚合到管理员侧边栏的一个子菜单下）
  {
    path: '/admin/user-interface',
    component: Layout,
    redirect: '/admin/user-interface/profile',
    alwaysShow: true,
    name: 'AdminUserInterface',
    meta: {
      title: '用户界面',
      icon: 'el-icon-s-operation',
      roles: ['admin']
    },
    children: [
      {
        path: 'profile',
        component: () => import('@/views/user/profile'),
        name: 'AdminUserProfile',
        meta: {
          title: '个人中心',
          icon: 'el-icon-user',
          roles: ['admin']
        }
      },
      {
        path: 'problem-records',
        component: () => import('@/views/user/problemRecords'),
        name: 'AdminProblemRecords',
        meta: {
          title: '题目学习记录',
          icon: 'el-icon-document',
          roles: ['admin']
        }
      },
      {
        path: 'experiment-records',
        component: () => import('@/views/experiment/records'),
        name: 'AdminExperimentRecords',
        meta: {
          title: '实验记录',
          icon: 'el-icon-tickets',
          roles: ['admin']
        }
      },
      {
        path: 'algorithm',
        component: () => import('@/views/algorithm/learning'),
        name: 'AdminAlgorithm',
        meta: {
          title: '算法学习',
          icon: 'el-icon-notebook-2',
          roles: ['admin']
        }
      },
      {
        path: 'visualization',
        component: () => import('@/views/visualization/index'),
        name: 'AdminVisualization',
        meta: {
          title: '算法可视化',
          icon: 'el-icon-video-play',
          roles: ['admin']
        }
      }
    ]
  },

  // 404 page must be placed at the end !!!
  { path: '*', redirect: '/404', hidden: true }
]

/**
 * asyncRoutes
 * 毕业设计演示中不需要动态路由，所以留空
 */
export const asyncRoutes = []

const createRouter = () => new Router({
  // mode: 'history', // require service support
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRoutes
})

const router = createRouter()

// Detail see: https://github.com/vuejs/vue-router/issues/1234#issuecomment-357941465
export function resetRouter() {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher // reset router
}

export default router
