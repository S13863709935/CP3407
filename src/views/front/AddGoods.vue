<template>
  <div style="width: 50%; margin: 10px auto">
    <div style="margin-bottom: 10px">
      <el-button @click="$router.back()"><< Back</el-button>
    </div>

    <div class="card">
      <el-form :model="form" label-width="120px" style="padding-right: 50px" :rules="rules" ref="formRef">
        <div style="text-align: center; font-size: 24px; margin: 30px 0">List Item</div>
        <el-form-item label="Name" prop="name">
          <el-input v-model="form.name" placeholder="Name"></el-input>
        </el-form-item>
        <el-form-item label="Price" prop="price">
          <el-input v-model="form.price" placeholder="Price"></el-input>
        </el-form-item>
        <el-form-item label="Category" prop="category">
          <el-select v-model="form.category" style="width: 100%">
            <el-option v-for="item in categoryList" :key="item.id" :value="item.name"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="Image" prop="img">
          <el-upload
              :action="$baseUrl + '/files/upload'"
              :headers="{ token: user.token }"
              list-type="picture"
              :on-success="handleImgSuccess"
          >
            <el-button type="primary">Upload Image</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item label="Shipping Address" prop="address">
          <el-input v-model="form.address" placeholder="Shipping Address"></el-input>
        </el-form-item>
        <el-form-item label="Sale Status" prop="saleStatus">
          <el-radio-group v-model="form.saleStatus">
            <el-radio :label="listingStatus.LISTED">On-shelf</el-radio>
            <el-radio :label="listingStatus.OFF_SHELF">Off-shelf</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="Details" prop="content">
          <div id="editor"></div>
        </el-form-item>
        <div style="text-align: center; margin-bottom: 20px">
          <el-button size="medium" type="primary" @click="save">Confirm Publish</el-button>
        </div>
      </el-form>
    </div>

  </div>
</template>

<script>
import E from "wangeditor"
const { LISTING_STATUS, normaliseListingStatus } = require('@/utils/listingStatus')

export default {
  name: "AddGoods",
  data() {
    return {
      id: this.$route.query.id,
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      categoryList: [],
      listingStatus: LISTING_STATUS,
      form: {
        saleStatus: LISTING_STATUS.OFF_SHELF
      },
      rules: {
        name: [
          { required: true, message: 'Please enter item name', trigger: 'blur' }
        ],
        price: [
          { required: true, message: 'Please enter item price', trigger: 'blur' }
        ],
        category: [
          { required: true, message: 'Please select category', trigger: 'blur' }
        ],
        address: [
          { required: true, message: 'Please enter shipping address', trigger: 'blur' }
        ]
      },
      editor: null,
    }
  },
  mounted() {
    this.loadCategory()
    this.loadGoods()
  },
  methods: {
    loadGoods() {
      if (this.id) {   // In case of Edit
        this.$request.get('/goods/selectById/' + this.id).then(res => {
          this.form = res.data || {}
          this.form.saleStatus = normaliseListingStatus(this.form.saleStatus)
          this.setRichText(this.form.content)
        })
      } else {
        this.setRichText('')
      }
    },
    loadCategory() {
      this.$request.get('/category/selectAll').then(res => {
        this.categoryList = res.data || []
      })
    },
    save() {   // Logic triggered by save button, it triggers Create or update
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          this.form.content = this.editor.txt.html()
          this.$request({
            url: this.form.id ? '/goods/update' : '/goods/add',
            method: this.form.id ? 'PUT' : 'POST',
            data: this.form
          }).then(res => {
            if (res.code === '200') {  // Indicates successful save
              this.$message.success('Published successfully')
            } else {
              this.$message.error(res.msg)  // Pop up error message
            }
          })
        }
      })
    },
    handleImgSuccess(response, file, fileList) {
      this.form.img = response.data
    },
    setRichText(html) {
      this.$nextTick(() => {
        this.editor = new E(`#editor`)
        this.editor.config.uploadImgServer = this.$baseUrl + '/files/editor/upload'
        this.editor.config.uploadFileName = 'file'
        this.editor.config.uploadImgHeaders = {
          token: this.user.token
        }
        this.editor.config.uploadImgParams = {
          type: 'img',
        }
        this.editor.create()  // Create
        this.editor.txt.html(html)
      })
    },
  }
}
</script>

<style scoped>

</style>
