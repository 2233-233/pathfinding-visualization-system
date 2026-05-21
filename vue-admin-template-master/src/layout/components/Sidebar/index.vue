<template>
  <div :class="{'has-logo':showLogo}">
    <logo v-if="showLogo" :collapse="isCollapse" />
    <el-scrollbar wrap-class="scrollbar-wrapper">
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :background-color="variables.menuBg"
        :text-color="variables.menuText"
        :unique-opened="false"
        :active-text-color="variables.menuActiveText"
        :collapse-transition="false"
        mode="vertical"
      >
        <sidebar-item
          v-for="route in routes"
          :key="route.path"
          :item="route"
          :base-path="route.path"
        />
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import Logo from './Logo'
import SidebarItem from './SidebarItem'
import variables from '@/styles/variables.scss'

export default {
  components: { SidebarItem, Logo },
  computed: {
    ...mapGetters([
      'sidebar'
    ]),
    routes() {
      // 直接从路由中获取所有路由，然后根据角色过滤
      const role = localStorage.getItem('user-role') || 'student'
      const allRoutes = this.$router.options.routes

      console.log('侧边栏 - 当前角色:', role)
      console.log('侧边栏 - 所有路由:', allRoutes)

      // 过滤路由，只显示当前角色有权限访问的路由
      return allRoutes.filter(route => {
        // 隐藏的路由不显示
        if (route.hidden) {
          return false
        }

        // 检查路由的meta.roles属性
        if (route.meta && route.meta.roles) {
          return route.meta.roles.includes(role)
        }

        // 根据路径判断角色权限
        if (route.path.startsWith('/admin')) {
          // 管理员路由
          return role === 'admin'
        } else {
          // 学生路由（非管理员路由）
          return role !== 'admin'
        }
      })
    },
    activeMenu() {
      const route = this.$route
      const { meta, path } = route
      // if set path, the sidebar will highlight the path you set
      if (meta.activeMenu) {
        return meta.activeMenu
      }
      return path
    },
    showLogo() {
      return this.$store.state.settings.sidebarLogo
    },
    variables() {
      return variables
    },
    isCollapse() {
      return !this.sidebar.opened
    }
  }
}
</script>
