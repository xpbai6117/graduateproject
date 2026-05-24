// app.js
App({
  onLaunch() {
  // 展示本地存储能力
    const logs = wx.getStorageSync('logs') || []
    logs.unshift(Date.now())
    wx.setStorageSync('logs', logs)


    wx.login({
      success: res => {
        wx.request({
          url: this.globalData.baseUrl +'/api/wx/authCode2Session?jsCode=' + res.code,
          method: 'get',
          success: (re) => {
            if (re.data.status == 0) {
              wx.setStorage({
                key: 'openid',
                data: re.data.data.openid
              })
                wx.setStorage({
                key: 'userInfo',
                data: re.data.data
              })
                wx.setStorage({
                key: 'token',
                data: re.data.data.token
              })
              if(this.loginCallback){
                this.loginCallback(1);
              }
            //   if (re.data.tel == '' || re.data.tel == null){
            //     console.log('notel'+re.data.tel)
            //     wx.navigateTo({
            //       url: '/pages/bind/bind',
            //     })
            //     // wx.showModal({
            //     //   title: '绑定用户',
            //     //   content: '您还未绑定手机号是否前往绑定',
            //     //   complete: (res) => {
            //     //     if (res.cancel) {
            //     //       wx.navigateTo({
            //     //         url: '/pages/bind/bind',
            //     //       })
            //     //     }
                
            //     //     if (res.confirm) {
                      
            //     //     }
            //     //   }
            //     // })
            // }else{
            //   wx.hidenM
            //   // 存储用户信息
            //   wx.setStorage({
            //     key: 'userInfo',
            //     data: rest.data
            //   })
            //   // 存储token
            //   wx.setStorage({
            //     key: 'token',
            //     data: rest.data.token
            //   })

            //   // wx.setStorage({
            //   //   key: 'phone',
            //   //   data: rest.data.tel
            //   // })

            //   wx.switchTab({
            //     url: '/pages/index/index',
            //   })
            // }
            }
            // else{

            //   wx.showToast({
            //     title:  res.data.msg,
            //     icon: 'none'
            //   });
            // }
          }
        })
      }
    }),
    wx.getSetting({
      withSubscriptions: true,   //  这里设置为true,下面才会返回mainSwitch
      success: function(res){   
      
        // 调起授权界面弹窗
				if (res.subscriptionsSetting.mainSwitch) {  // 用户打开了订阅消息总开关
					console.log(res.subscriptionsSetting.itemSettings,'1111')
          if (res.subscriptionsSetting.itemSettings != null) {   // 用户同意总是保持是否推送消息的选择, 这里表示以后不会再拉起推送消息的授权
            let moIdState = res.subscriptionsSetting.itemSettings['ROQpIFNfu_LWiP4RbvWxaqs64UpI4m1_ZXVXy44_bBM','mosilY5vhEBdgCUNyfFwDv11Lc2WBMeSuZ5JShl0HhA'];  // 用户同意的消息模板id
            if(moIdState === 'accept'){   
              console.log('接受了消息推送');

            }else if(moIdState === 'reject'){
              console.log("拒绝消息推送");

            }else if(moIdState === 'ban'){
              console.log("已被后台封禁");

            }
          }else {
							// 当用户没有点击 ’总是保持以上选择，不再询问‘  按钮。那每次执到这都会拉起授权弹窗
							
							// wx.requestSubscribeMessage({   // 调起消息订阅界面
							// 	tmplIds: ['ROQpIFNfu_LWiP4RbvWxaqs64UpI4m1_ZXVXy44_bBM','mosilY5vhEBdgCUNyfFwDv11Lc2WBMeSuZ5JShl0HhA'],
							// 	success (res) { 
							// 		console.log('订阅消息 成功 ');
							// 		console.log(res);
							// 	},
							// 	fail (er){
							// 		console.log("订阅消息 失败 ");
							// 		console.log(er);
							// 	}
							// }) 
            wx.showModal({
              title: '提示',
              content:'请授权开通服务通知',
              showCancel: true,
              success: function (ress) {
                if (ress.confirm) {  
                  wx.requestSubscribeMessage({   // 调起消息订阅界面
                    tmplIds: ['ROQpIFNfu_LWiP4RbvWxaqs64UpI4m1_ZXVXy44_bBM','mosilY5vhEBdgCUNyfFwDv11Lc2WBMeSuZ5JShl0HhA'],
                    success (res) { 
                      console.log('订阅消息 成功 ');
                      console.log(res);
                    },
                    fail (er){
                      console.log("订阅消息 失败 ");
                      console.log(er);
                    }
                  })     
                        
                }
              }
            })
          }

        }else {
          console.log('订阅消息未开启')
        }      
      },
      fail: function(error){
        console.log(error);
      },
    })
  },
  globalData: {
		userInfo: null,
		baseUrl: "https://www.benben-x.cn"
		// baseUrl: "http://localhost:8080"
  },

  
})
