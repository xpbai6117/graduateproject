// pages/register/register.js
Page({

  /**
   * 页面的初始数据
   */
  data: {

  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {

  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady() {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow() {

  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide() {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload() {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh() {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom() {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage() {

  },

  register: function (e) {
    let username = e.detail.value.username;
    let pwd = e.detail.value.pwd;
    let pwdConfirm = e.detail.value.pwdConfirm;
    let openId = wx.getStorageSync('openid')
    if (username === '' || username == null) {
      wx.showToast({
        title: '账号不能为空',
        icon: 'none'
      })
      return;
    }
    if (pwd === '' || pwd == null) {
      wx.showToast({
        title: '密码不能为空',
        icon: 'none'
      })
      return;
    }
    if (pwdConfirm === '' || pwdConfirm == null) {
      wx.showToast({
        title: '确认密码不能为空',
        icon: 'none'
      })
      return;
    }
    if (pwd !== pwdConfirm){
      wx.showToast({
        title: '两次输入的密码不一致',
        icon: 'none'
      })
      return;
    }

    wx.request({
       url: 'https://www.benben-x.cn/api/user/register?username='+username+'&pwd='+pwd+"&pwdConfirm="+pwdConfirm+"&openId="+openId,
       //url: 'http://localhost:8080/api/user/register?username='+username+'&pwd='+pwd+"&pwdConfirm="+pwdConfirm,
       method: "get",
       success: (res) => {
         let rest = res.data;
         console.log(rest)
         if (res.data.status == 0) {
           wx.switchTab({
             url: '/pages/index/index',
           })
         } else {
           wx.showToast({
             title: res.data.msg,
             icon: 'none'
           });
         }
       }
     })
  }
})