<template>
  <div style="width: 85%; margin: 10px auto" class="card">

    <div style="margin-bottom: 15px; display: flex">
      <div style="flex: 1">
        <el-radio-group v-model="url" @change="load(1)">
          <el-radio-button label="selectPage">My Purchases</el-radio-button>
          <el-radio-button label="selectSalePage">My Sales</el-radio-button>
        </el-radio-group>
      </div>
      <div>
        <el-input v-model="goodsName" placeholder="Enter item name" style="width: 200px; margin-right: 5px" clearable></el-input>
        <el-input v-model="orderNo" placeholder="Enter order number" style="width: 200px; margin-right: 5px" clearable></el-input>
        <el-select v-model="status" style="width: 200px; margin-right: 5px" clearable>
          <el-option label="Cancelled" value="已取消"></el-option>
          <el-option label="Pending Payment" value="待支付"></el-option>
          <el-option label="To Ship" value="待发货"></el-option>
          <el-option label="To Receive" value="待收货"></el-option>
          <el-option label="Completed" value="已完成"></el-option>
        </el-select>
        <el-button type="primary" @click="load(1)">Search</el-button>
        <el-button type="warning" @click="reset">Reset</el-button>
      </div>
    </div>

    <div style="margin: 10px 0">
      <el-table :data="tableData" strip >
        <el-table-column prop="id" label="ID" width="70" align="center" sortable></el-table-column>
        <el-table-column prop="goodsName" label="Item Name" show-overflow-tooltip></el-table-column>
        <el-table-column prop="goodsImg" label="Item Image">
          <template v-slot="scope">
            <el-image v-if="scope.row.goodsImg" style="width: 50px" :src="scope.row.goodsImg" :preview-src-list="[scope.row.goodsImg]"></el-image>
          </template>
        </el-table-column>
        <el-table-column prop="orderNo" label="Order No." show-overflow-tooltip></el-table-column>
        <el-table-column prop="total" label="Total Price">
          <template v-slot="scope">
            <span style="color: red">${{ scope.row.total }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="time" label="Order Time" show-overflow-tooltip></el-table-column>
        <el-table-column prop="payNo" label="Payment No." show-overflow-tooltip></el-table-column>
        <el-table-column prop="payTime" label="Payment Time" show-overflow-tooltip></el-table-column>
        <el-table-column prop="user" label="Buyer Name" show-overflow-tooltip></el-table-column>
        <el-table-column prop="address" label="Shipping Address" show-overflow-tooltip></el-table-column>
        <el-table-column prop="phone" label="Contact Info" show-overflow-tooltip></el-table-column>
        <el-table-column prop="userName" label="Receiver Name"></el-table-column>
        <el-table-column prop="status" label="Status">
          <template v-slot="scope">
            <el-tag type="danger" v-if="scope.row.status === '已取消'">Cancelled</el-tag>
            <el-tag type="danger" v-if="scope.row.status === '待支付'">Pending Payment</el-tag>
            <el-tag type="primary" v-if="scope.row.status === '待发货'">To Ship</el-tag>
            <el-tag type="info" v-if="scope.row.status === '待收货'">To Receive</el-tag>
            <el-tag type="success" v-if="scope.row.status === '已完成'">Completed</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="saleName" label="Seller Name"></el-table-column>
        <el-table-column label="Actions" align="center" width="300">
          <template v-slot="scope">
            <el-button v-if="scope.row.status === '待支付' && scope.row.userId === user.id" size="mini" type="primary" plain @click="pay(scope.row.orderNo)">Pay</el-button>
            <el-button v-if="scope.row.status === '待支付'" size="mini" type="danger" plain @click="changeStatus(scope.row, '已取消')">Cancel</el-button>
            <el-button v-if="scope.row.status === '待发货' && scope.row.saleId === user.id" size="mini" type="info" plain @click="changeStatus(scope.row, '待收货')">Ship</el-button>
            <el-button v-if="scope.row.status === '待收货' && scope.row.userId === user.id" size="mini" type="primary" plain @click="changeStatus(scope.row, '已完成')">Receive</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div style="margin: 15px 0">
        <el-pagination
            background
            @current-change="handleCurrentChange"
            :current-page="pageNum"
            :page-sizes="[5, 10, 20]"
            :page-size="pageSize"
            layout="total, prev, pager, next"
            :total="total">
        </el-pagination>
      </div>
    </div>

    <el-dialog title="Order Information" :visible.sync="fromVisible" width="40%" :close-on-click-modal="false" destroy-on-close>
      <el-form :model="form" label-width="120px" style="padding-right: 50px" :rules="rules" ref="formRef">
        <el-form-item label="Item Name" prop="goodsName">
          <el-input v-model="form.goodsName" placeholder="Item Name"></el-input>
        </el-form-item>
        <el-form-item label="Item Image" prop="goodsImg">
          <el-input v-model="form.goodsImg" placeholder="Item Image"></el-input>
        </el-form-item>
        <el-form-item label="Order No." prop="orderNo">
          <el-input v-model="form.orderNo" placeholder="Order No."></el-input>
        </el-form-item>
        <el-form-item label="Total Price" prop="total">
          <el-input v-model="form.total" placeholder="Total Price"></el-input>
        </el-form-item>
        <el-form-item label="Order Time" prop="time">
          <el-input v-model="form.time" placeholder="Order Time"></el-input>
        </el-form-item>
        <el-form-item label="Payment No." prop="payNo">
          <el-input v-model="form.payNo" placeholder="Payment No."></el-input>
        </el-form-item>
        <el-form-item label="Payment Time" prop="payTime">
          <el-input v-model="form.payTime" placeholder="Payment Time"></el-input>
        </el-form-item>
        <el-form-item label="Buyer ID" prop="userId">
          <el-input v-model="form.userId" placeholder="Buyer ID"></el-input>
        </el-form-item>
        <el-form-item label="Shipping Address" prop="address">
          <el-input v-model="form.address" placeholder="Shipping Address"></el-input>
        </el-form-item>
        <el-form-item label="Contact Info" prop="phone">
          <el-input v-model="form.phone" placeholder="Contact Info"></el-input>
        </el-form-item>
        <el-form-item label="Receiver Name" prop="userName">
          <el-input v-model="form.userName" placeholder="Receiver Name"></el-input>
        </el-form-item>
        <el-form-item label="Order Status" prop="status">
          <el-input v-model="form.status" placeholder="Order Status"></el-input>
        </el-form-item>
        <el-form-item label="Seller ID" prop="saleId">
          <el-input v-model="form.saleId" placeholder="Seller ID"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="fromVisible = false">Cancel</el-button>
        <el-button type="primary" @click="save">Confirm</el-button>
      </div>
    </el-dialog>


  </div>
</template>
<script>
export default {
  name: "Orders",
  data() {
    return {
      url: 'selectPage',
      tableData: [],  // All data
      pageNum: 1,   // Current page number
      pageSize: 10,  // Number of items displayed per page
      total: 0,
      fromVisible: false,
      form: {},
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      rules: {
      },
      ids: [],
      goodsName: null,
      orderNo: null,
      status: null
    }
  },
  created() {
    this.load(1)
  },
  methods: {
    pay(orderNo) {
      window.open(this.$baseUrl.replace(/\/+$/, '') + '/alipay/pay?orderNo=' + encodeURIComponent(orderNo))
    },
    changeStatus(row, status) {
      this.$confirm('Are you sure you want to proceed?', 'Confirm Action', {type: "warning"}).then(response => {
        this.form = JSON.parse(JSON.stringify(row))
        this.form.status = status
        this.$request.put('/orders/update', this.form).then(res => {
          if (res.code === '200') {  // Indicates successful save
            this.$message.success('Operation successful')
            this.load(1)
          } else {
            this.$message.error(res.msg)  // Pop up error message
          }
        })
      }).catch(e => {})
    },
    handleAdd() {   // Create data
      this.form = {}  // Clear data when creating
      this.fromVisible = true   // Open modal
    },
    handleEdit(row) {   // Edit data
      this.form = JSON.parse(JSON.stringify(row))  // Assign value to form object. Note: deep copy is required
      this.fromVisible = true   // Open modal
    },
    save() {   // Logic triggered by save button, it triggers create or update
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          this.$request({
            url: this.form.id ? '/orders/update' : '/orders/add',
            method: this.form.id ? 'PUT' : 'POST',
            data: this.form
          }).then(res => {
            if (res.code === '200') {  // Indicates successful save
              this.$message.success('Saved successfully')
              this.load(1)
              this.fromVisible = false
            } else {
              this.$message.error(res.msg)  // Pop up error message
            }
          })
        }
      })
    },
    del(id) {   // Single delete
      this.$confirm('Are you sure you want to delete?', 'Confirm Delete', {type: "warning"}).then(response => {
        this.$request.delete('/orders/delete/' + id).then(res => {
          if (res.code === '200') {   // Indicates successful operation
            this.$message.success('Operation successful')
            this.load(1)
          } else {
            this.$message.error(res.msg)  // Pop up error message
          }
        })
      }).catch(() => {
      })
    },
    handleSelectionChange(rows) {   // All currently selected row data
      this.ids = rows.map(v => v.id)
    },
    delBatch() {   // Batch delete
      if (!this.ids.length) {
        this.$message.warning('Please select data')
        return
      }
      this.$confirm('Are you sure you want to batch delete these items?', 'Confirm Delete', {type: "warning"}).then(response => {
        this.$request.delete('/orders/delete/batch', {data: this.ids}).then(res => {
          if (res.code === '200') {   // Indicates successful operation
            this.$message.success('Operation successful')
            this.load(1)
          } else {
            this.$message.error(res.msg)  // Pop up error message
          }
        })
      }).catch(() => {
      })
    },
    load(pageNum) {  // Paginated search
      if (pageNum) this.pageNum = pageNum
      this.$request.get('/orders/' + this.url, {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          goodsName: this.goodsName,
          status: this.status,
          orderNo: this.orderNo,
        }
      }).then(res => {
        if (res.code === '200') {
          this.tableData = res.data?.list || []
          this.total = res.data?.total || 0
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    reset() {
      this.goodsName = null
      this.orderNo = null
      this.status = null
      this.load(1)
    },
    handleCurrentChange(pageNum) {
      this.load(pageNum)
    },
  }
}
</script>

<style scoped>

</style>
