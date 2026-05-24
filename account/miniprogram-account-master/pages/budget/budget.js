
const { getRequest, postRequest } = require("../../utils/request");

Page({

  /**
   * 页面的初始数据
   */
  
  data: {
    budget: '',
		budgetInfo: '',
		budgetId: '',
		checkShow: false,
    bookList: [],
    checked: true,
    bookId: '',
    bookData: {},
    moneyShow: false,
    show: true,
    duration: 400,
    position: 'bottom',
    round: true,
    overlay: true,
    customStyle: 'height: 60%;',
    overlayStyle: 'background-color: rgba(0, 0, 0, 0.7)',
		bookIndex: 0,
		budgetStatus: 1
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {

    let bookId = wx.getStorageSync('bookId')
    let budgetInfo = '';
    getRequest("/api/book/budget/" + bookId).then(res=> {
			if (res.status == 0) {
				budgetInfo = res.data
				//console.log(budgetInfo)
				let checkShow = budgetInfo == null ? false : true
                let budgetId = budgetInfo == null ? '' : budgetInfo.id
                let checked = true
                if(budgetInfo != null){
                    checked = budgetInfo.status == 1 ? true : false
                }
				this.setData({
					budgetInfo: budgetInfo,
					checkShow: checkShow,
                    budgetId: budgetId,
                    checked: checked
				})
			}
		})
		this.setData({
            bookId,
			show: false,
		})	
    //   getRequest("/api/book/get").then(res => {
    //     if (res.status == 0) {
    //       let rest = res.data;
    //       console.log(rest)
    //       this.setData({
	// 					bookList: rest,
	// 					budgetInfo: null
    //       })
    //     }
    //   })


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
  bindBookChange(e) {
    // 账本集合下标
    let index = e.currentTarget.dataset.value
    let bookId = wx.getStorageSync('bookId')
    let budgetInfo = '';
    getRequest("/api/book/budget/" + bookId).then(res=> {
			if (res.status == 0) {
				budgetInfo = res.data
				//console.log(budgetInfo)
				let checkShow = budgetInfo == null ? false : true
				let budgetId = budgetInfo == null ? '' : budgetInfo.id
				this.setData({
					budgetInfo: budgetInfo,
					checkShow: checkShow,
					budgetId: budgetId
				})
			}
		})
		this.setData({
      bookId,
			show: false,
		})		
  },
  editbudget: function (e) {
		let budget = e.detail.value.budget;
		let budgetId = this.data.budgetId;
        let budgetStatus = this.data.budgetStatus
        let bookId = wx.getStorageSync('bookId')
    postRequest("/api/book/budget",{
      budget: budget * 100,
			bookId: bookId,
			id: budgetId,
			status: budgetStatus
    }).then(res => {
      if (res.status == 0) {
        wx.navigateBack()
      }
    })
	},
	budgetStatusChange(e) {
		let budgetStatus = e.detail.value ? 1 : 2;
		this.setData({
			budgetStatus
		})
	}
})