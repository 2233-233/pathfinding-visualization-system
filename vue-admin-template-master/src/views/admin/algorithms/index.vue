<script setup>

</script>

<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input
        v-model="searchName"
        placeholder="算法名称"
        style="width: 200px;"
        class="filter-item"
        @keyup.enter.native="handleSearch"
      />
      <el-button
        class="filter-item"
        type="primary"
        icon="el-icon-search"
        @click="handleSearch"
      >
        搜索
      </el-button>
      <el-button
        class="filter-item"
        style="margin-left: 10px;"
        type="primary"
        icon="el-icon-plus"
        @click="handleCreate"
      >
        添加算法
      </el-button>
    </div>

    <el-table
      v-loading="loading"
      :data="filteredAlgorithms"
      border
      fit
      highlight-current-row
      style="width: 100%;"
    >
      <el-table-column label="ID" prop="algorithmId" align="center" width="80">
        <template slot-scope="{row}">
          <span>{{ row.algorithmId }}</span>
        </template>
      </el-table-column>

      <el-table-column label="算法名称" min-width="150">
        <template slot-scope="{row}">
          <span class="link-type" @click="handleUpdate(row)">{{ row.name }}</span>
        </template>
      </el-table-column>

      <el-table-column label="算法描述" min-width="200">
        <template slot-scope="{row}">
          <span>{{ row.description }}</span>
        </template>
      </el-table-column>

      <el-table-column label="时间复杂度" width="120">
        <template slot-scope="{row}">
          <span>{{ row.complexity }}</span>
        </template>
      </el-table-column>

      <el-table-column label="操作" align="center" width="200" class-name="small-padding fixed-width">
        <template slot-scope="{row}">
          <el-button
            type="primary"
            size="mini"
            @click="handleUpdate(row)"
          >
            编辑
          </el-button>
          <el-button
            size="mini"
            type="danger"
            @click="handleDelete(row)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加/编辑算法对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px">
      <el-form
        ref="algorithmForm"
        :rules="rules"
        :model="formData"
        label-width="100px"
      >
        <el-form-item label="算法名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入算法名称" />
        </el-form-item>

        <el-form-item label="算法描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入算法描述"
          />
        </el-form-item>

        <el-form-item label="时间复杂度" prop="complexity">
          <el-input v-model="formData.complexity" placeholder="例如：O(V+E)" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">
          取消
        </el-button>
        <el-button
          type="primary"
          @click="saveAlgorithm"
        >
          保存
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
// 算法模块已移除，使用模拟数据
export default {
  name: 'AlgorithmManagement',
  data() {
    return {
      algorithms: [],
      searchName: '',
      dialogVisible: false,
      dialogTitle: '添加算法',
      formData: {
        algorithmId: null,
        name: '',
        description: '',
        complexity: ''
      },
      rules: {
        name: [{ required: true, message: '请输入算法名称', trigger: 'blur' }],
        description: [{ required: true, message: '请输入算法描述', trigger: 'blur' }],
        complexity: [{ required: true, message: '请输入时间复杂度', trigger: 'blur' }]
      },
      loading: false
    }
  },
  computed: {
    filteredAlgorithms() {
      if (!this.searchName) {
        return this.algorithms
      }
      return this.algorithms.filter(item =>
        item.name.toLowerCase().includes(this.searchName.toLowerCase())
      )
    }
  },
  created() {
    this.loadAlgorithms()
  },
  methods: {
    // 加载算法数据 - 使用模拟数据
    async loadAlgorithms() {
      this.loading = true
      try {
        // 模拟算法数据（与数据库algorithm表ID对应：1=A*, 2=Dijkstra, 4=DFS, 5=BFS）
        this.algorithms = [
          { algorithmId: 1, name: 'A*', description: '启发式搜索算法', complexity: 'O(b^d)' },
          { algorithmId: 2, name: 'Dijkstra', description: '迪杰斯特拉最短路径算法', complexity: 'O((V+E)logV)' },
          { algorithmId: 4, name: 'DFS', description: '深度优先搜索算法', complexity: 'O(V+E)' },
          { algorithmId: 5, name: 'BFS', description: '广度优先搜索算法', complexity: 'O(V+E)' }
        ]
      } catch (error) {
        console.error('加载算法数据失败:', error)
        this.$message.error('加载算法数据失败')
      } finally {
        this.loading = false
      }
    },

    async handleSearch() {
      if (!this.searchName || !this.searchName.trim()) {
        await this.loadAlgorithms() // ✅ 正确
        return
      }

      try {
        // 模拟搜索
        const searchTerm = this.searchName.trim().toLowerCase()
        this.algorithms = [
          { algorithmId: 1, name: 'A*', description: '启发式搜索算法', complexity: 'O(b^d)' },
          { algorithmId: 2, name: 'Dijkstra', description: '迪杰斯特拉最短路径算法', complexity: 'O((V+E)logV)' },
          { algorithmId: 4, name: 'DFS', description: '深度优先搜索算法', complexity: 'O(V+E)' },
          { algorithmId: 5, name: 'BFS', description: '广度优先搜索算法', complexity: 'O(V+E)' }
        ].filter(item => item.name.toLowerCase().includes(searchTerm))
      } catch (err) {
        console.error(err)
        this.$message.error('搜索失败')
      }
    },

    // 打开添加对话框
    handleCreate() {
      this.dialogTitle = '添加算法'
      this.formData = {
        algorithmId: null,
        name: '',
        description: '',
        complexity: ''
      }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.algorithmForm && this.$refs.algorithmForm.clearValidate()
      })
    },

    // 打开编辑对话框
    handleUpdate(row) {
      this.dialogTitle = '编辑算法'
      this.formData = {
        algorithmId: row.algorithmId,
        name: row.name,
        description: row.description,
        complexity: row.complexity
      }
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.algorithmForm && this.$refs.algorithmForm.clearValidate()
      })
    },

    // 保存算法 - 使用模拟数据
    async saveAlgorithm() {
      this.$refs.algorithmForm.validate(async(valid) => {
        if (valid) {
          try {
            if (this.formData.algorithmId) {
              // 编辑 - 模拟更新
              const index = this.algorithms.findIndex(item => item.algorithmId === this.formData.algorithmId)
              if (index !== -1) {
                this.algorithms[index] = {
                  ...this.algorithms[index],
                  name: this.formData.name,
                  description: this.formData.description,
                  complexity: this.formData.complexity
                }
                this.$message.success('更新成功')
              }
            } else {
              // 添加 - 模拟创建
              const newId = this.algorithms.length > 0 ? Math.max(...this.algorithms.map(item => item.algorithmId)) + 1 : 1
              this.algorithms.push({
                algorithmId: newId,
                name: this.formData.name,
                description: this.formData.description,
                complexity: this.formData.complexity
              })
              this.$message.success('创建成功')
            }

            this.dialogVisible = false
          } catch (error) {
            console.error('保存算法失败:', error)
            this.$message.error('保存算法失败')
          }
        }
      })
    },

    // 删除算法 - 使用模拟数据
    async handleDelete(row) {
      this.$confirm('确定要删除该算法吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async() => {
        try {
          // 模拟删除
          const index = this.algorithms.findIndex(item => item.algorithmId === row.algorithmId)
          if (index !== -1) {
            this.algorithms.splice(index, 1)
            this.$message.success('删除成功')
          }
        } catch (error) {
          console.error('删除算法失败:', error)
          this.$message.error('删除算法失败')
        }
      })
    }
  }
}
</script>

<style scoped>
.app-container {
  padding: 20px;
}

.filter-container {
  margin-bottom: 20px;
}

.filter-item {
  margin-right: 10px;
}

.link-type {
  color: #409EFF;
  cursor: pointer;
}

.link-type:hover {
  text-decoration: underline;
}

.dialog-footer {
  text-align: right;
}
</style>
