<template>
  <div style="width: 60%; margin: 10px auto">

    <div style="display: flex; grid-gap: 20px">

      <div class="chat-card" style="width: 200px; min-height: 600px">
        <div v-if="chatGroupList.length > 0" @click="loadChatList(item.chatUserId)" v-for="item in chatGroupList" :key="item.id">
          <div style="position: relative" v-if="item.userId === user.id" class="chat-group" :class="{'chat-group-active' : item.chatUserId === activeChatUserId}">
            <img :src="item.chatUserAvatar" style="width: 50px; height: 50px; border-radius: 50%" />
            <el-badge :value="item.chatNum" :max="99" v-if="item.chatNum > 0 && !activeChatUserId">
              <span style="color: #333">{{ item.chatUserName }}</span>
            </el-badge>
            <span v-else style="color: #333">{{ item.chatUserName }}</span>

            <i class="el-icon-close" style="position: absolute; top: 10px; right: 10px" @click.stop="closeGroup(item.id)"></i>
          </div>
        </div>
        <div v-if="chatGroupList.length === 0" style="color: #666; padding: 20px 0; text-align: center">No chat contacts..</div>
      </div>
      <div class="chat-card" style="flex: 1; height: 600px">
        <div class="chat-text" style="height: 400px; border-bottom: 1px solid #ddd; overflow-y: auto;">
          <div v-for="item in chatList" :key="item.id" style="margin-bottom: 20px">

            <div v-if="item.chatUserId === user.id" style="display: flex; grid-gap: 10px">
              <img style="width: 50px; height: 50px; border-radius: 5px" :src="item.userAvatar" alt="">
              <div style="max-width: 50%; width: fit-content">
                <div style="color: #666; margin-bottom: 5px">{{ item.userName }}</div>
                <div style="background-color: #d1e7ff; padding: 10px; border-radius: 5px;" v-text="item.text"></div>
              </div>
            </div>
            <div v-if="item.userId === user.id" style="display: flex; justify-content: right; grid-gap: 10px">
              <div style="max-width: 50%; width: fit-content">
                <div style="color: #666; margin-bottom: 5px; text-align: right">{{ item.userName }}</div>
                <div style="background-color: #a3eecb; padding: 10px; border-radius: 5px;" v-text="item.text"></div>
              </div>
              <img style="width: 50px; height: 50px; border-radius: 5px" :src="item.userAvatar" alt="">
            </div>
            </div>
          <div style="height: 0"><a id="chat-text-end" name="1" href="#1" style="outline: none">&nbsp</a></div>
        </div>
        <div style="height: 200px">
          <textarea v-model="form.text" style="height: 160px; width: 100%; padding: 20px; border: none;
             border-bottom: 1px solid #ddd; outline: none"></textarea>
          <div style="text-align: right; padding-right: 10px">
            <el-button type="primary" size="mini" @click="send">Send</el-button>
          </div>
        </div>
        </div>

    </div>
  </div>
</template>

<script>
let socket

export default {
  name: "Chat",
  data() {
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      chatGroupList: [],
      activeChatUserId: 0,
      form: {},
      chatList: []
    }
  },
  created() {
    this.init()
    this.loadChatGroup()
  },
  methods: {
    closeGroup(id) {
      this.$request.delete('/chatGroup/delete/' + id).then(res => {
        this.loadChatGroup()
        this.chatList = []  // Clear Chat Information
      })
    },
    send() {
      if (!this.activeChatUserId) {
        this.$message.warning('Please select a chat contact')
        return
      }
      if (!this.form.text) {
        this.$message.warning('Please enter message content')
        return
      }
      this.form.chatUserId = this.activeChatUserId
      this.form.userId = this.user.id
      this.$request.post('/chatInfo/add', this.form).then(res => {
        this.form = {}
        this.loadChatList(this.activeChatUserId)

        // Send socket message
        socket.send('Chat Information')
      })
    },
    loadChatList(chatUserId) {
      this.form = {}
      this.activeChatUserId = chatUserId

      // Update message read status
      this.$request.put('/chatInfo/updateRead/' + chatUserId).then(res => {

        this.loadChatGroup()
      })
      this.$request.get('/chatInfo/selectUserChat/' + chatUserId).then(res => {
        this.chatList = res.data || []
        document.getElementById('chat-text-end').click()
      })
    },
    loadChatGroup() {
      this.$request.get('/chatGroup/selectUserGroup').then(res => {
        this.chatGroupList = res.data || []
      })
    },
    init() {
      let _this = this
      if (typeof (WebSocket) == "undefined") {
        console.log("Your browser does not support WebSocket")
      } else {
        console.log("Your browser supports WebSocket")
        const apiBaseUrl = (this.$baseUrl || 'http://localhost:9090').replace(/\/+$/, '')
        const socketBaseUrl = apiBaseUrl.replace(/^http:/, 'ws:').replace(/^https:/, 'wss:')
        let socketUrl = socketBaseUrl + "/chatServer/" + this.user.id
        if (socket != null) {
          socket.close()
          socket = null
        }
        // Start a websocket service
        socket = new WebSocket(socketUrl)
        // Open event
        socket.onopen = function () {
          console.log("websocket opened")
        }
        // Browser receives messages, gets the latest Chat Information from the server database
        socket.onmessage = function (msg) {
          _this.loadChatGroup()
          _this.loadChatList(_this.activeChatUserId)
        }
        // Close event
        socket.onclose = function () {
          console.log("websocket closed")
        }
        // Error event
        socket.onerror = function () {
          console.log("websocket error occurred")
        }
      }
    }
  }
}
</script>

<style scoped>
.chat-card {
  background-color: #fff;
  border-radius: 5px;
  box-shadow: 0 2px 5px 0 rgba(0, 0, 0, 0.1);
}
.chat-group {
  padding: 10px;
  cursor: pointer;
  display: flex;
  align-items: center;
  grid-gap: 10px;
  border-bottom: 1px solid #ddd
}
.chat-group-active {
  background-color: #d1e7ff;
}
.chat-text {
  padding: 20px;
}
.chat-text::-webkit-scrollbar {
  width: 10px;
}
.chat-text::-webkit-scrollbar-thumb {
  border-radius: 8px;
  background-color: #d1e7ff;
}
/deep/.el-badge__content.is-fixed {
  right: 2px;
}
</style>
