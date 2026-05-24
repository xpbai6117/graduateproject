// 配置讯飞API参数
const config = {
  hostUrl: "https://spark-api.xf-yun.com/v1.1/chat",
  appid: "77d5a727",
  apiSecret: "ZWY2OWQwN2Y5MDBhNmEyZGFkMmY0NzEx", 
  apiKey: "fa88d9df219efb0637a2318030353c6c",
  domain: "lite"
}

// 引入CryptoJS
const CryptoJS = require('../../utils/crypto-js.js');

Page({
  data: {
    messages: [],
    inputValue: '',
    toView: '',
    loading: false,
    socketTask: null
  },

  onLoad() {
    this.addMessage('我是讯飞星火助手，请问有什么可以帮助您？', 'ai');
  },

  onUnload() {
    this.closeSocket();
  },

// 修改closeSocket方法
closeSocket() {
  if (this.data.socketTask && this.data.socketTask.readyState === 1) {  // 只有OPEN状态才需要关闭
    this.data.socketTask.close({
      success: () => {
        console.log('WebSocket已关闭');
        this.setData({ socketTask: null });
      },
      fail: err => {
        console.log('关闭失败(无害):', err);  // 修改为log避免报错
        this.setData({ socketTask: null });
      }
    });
  } else {
    this.setData({ socketTask: null });
  }
},

	addMessage(content, type) {
		const newMessage = {
			id: 'msg-' + Date.now(),
			content,
			type
		};
		this.setData({
			messages: [...this.data.messages, newMessage],
			toView: 'msg-' + newMessage.id  // 确保使用完整ID
		}, () => {
			// 在回调中确保滚动执行
			this.scrollToBottom();
		});
	},
	

  onInput(e) {
    this.setData({ inputValue: e.detail.value });
  },

  sendMessage() {
    const message = this.data.inputValue.trim();
    if (!message) return;

    this.addMessage(message, 'user');
    this.setData({ inputValue: '', loading: true });

    this.connectWebSocket(message);
  },

  async connectWebSocket(message) {
    try {
      this.closeSocket();
      
      // 生成鉴权URL
      const date = new Date().toUTCString();
      const host = 'spark-api.xf-yun.com';
      const path = '/v1.1/chat';
      
      // 使用CryptoJS生成签名
      const signature = CryptoJS.enc.Base64.stringify(
        CryptoJS.HmacSHA256(
          `host: ${host}\ndate: ${date}\nGET ${path} HTTP/1.1`,
          config.apiSecret
        )
      );
      
      // 使用小程序API实现base64编码
      const authorization = wx.arrayBufferToBase64(
        new Uint8Array(
          Array.from(`api_key="${config.apiKey}", algorithm="hmac-sha256", headers="host date request-line", signature="${signature}"`)
          .map(c => c.charCodeAt(0))
        ).buffer
      );
      
      const url = `wss://${host}${path}?authorization=${encodeURIComponent(authorization)}&date=${encodeURIComponent(date)}&host=${host}`;

      // 创建WebSocket连接
      const socketTask = wx.connectSocket({
        url,
        header: { 'app-id': config.appid },
        success: () => console.log('开始连接WebSocket...'),
        fail: err => {
          console.error('连接失败:', err);
          this.addMessage('连接服务器失败', 'ai');
          this.setData({ loading: false });
        }
      });

      this.setData({ socketTask });

      // 监听WebSocket事件
      socketTask.onOpen(() => {
        console.log('WebSocket连接已建立');
        socketTask.send({
          data: JSON.stringify(this.buildRequestData(message)),
          success: () => console.log('消息发送成功'),
          fail: err => {
            console.error('发送失败:', err);
            this.addMessage('发送消息失败', 'ai');
            this.setData({ loading: false });
          }
        });
      });

      let fullReply = '';
      socketTask.onMessage((res) => {
        try {
          const data = JSON.parse(res.data);
          if (data.header.code !== 0) {
            throw new Error(`[${data.header.code}] ${data.header.message}`);
          }

          // 拼接流式返回内容
          fullReply += data.payload.choices.text[0].content;
          
          // 更新最后一条AI消息
          const messages = [...this.data.messages];
          const lastIndex = messages.length - 1;
          
          if (lastIndex >= 0 && messages[lastIndex].type === 'ai') {
            messages[lastIndex].content = fullReply;
          } else {
            messages.push({
              id: 'msg-' + Date.now(),
              content: fullReply,
              type: 'ai'
            });
          }
          
          this.setData({ messages }, () => {
            this.scrollToBottom();
          });
        } catch (err) {
          console.error('消息处理错误:', err);
        }
      });

      socketTask.onClose(() => {
        console.log('WebSocket连接已关闭');
        this.setData({ loading: false });
      });

      socketTask.onError((err) => {
        console.error('WebSocket错误:', err);
        this.addMessage('通信发生错误，请重试', 'ai');
        this.setData({ loading: false });
      });

    } catch (err) {
      console.error('连接异常:', err);
      this.addMessage('连接异常: ' + err.message, 'ai');
      this.setData({ loading: false });
    }
  },

  // 构建请求数据
  buildRequestData(question) {
    return {
      header: {
        app_id: config.appid,
        uid: 'user-' + Math.random().toString(36).substr(2, 8)
      },
      parameter: {
        chat: {
          domain: config.domain,
          temperature: 0.5,
          max_tokens: 2048
        }
      },
      payload: {
        message: {
          text: [{ role: 'user', content: question }]
        }
      }
    }
  },

// 增强的scrollToBottom方法
scrollToBottom() {
  if (this.data.messages.length > 0) {
    const lastMsgId = this.data.messages[this.data.messages.length - 1].id;
    this.setData({
      toView: lastMsgId
    }, () => {
      // 添加延时确保滚动生效
      setTimeout(() => {
        this.setData({ toView: lastMsgId });
      }, 100);
    });
  }
}
});