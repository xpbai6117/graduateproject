let c = require('../../utils/common.js');
import {
  postRequest,
  getRequest
} from '../../utils/request'
Page({

  /**
   * 页面的初始数据
   */
  data: {
    detailDesc: '',
    initData: {},
    userData: [],
    initUserInfo: {
      initUserId: null,
      initUserName: null,
    },
    day: '',
    show: false,
    bookTime: new Date().getTime(),
    formatter(type, value) {
      if (type === 'year') {
        return `${value}年`;
      } else if (type === 'month') {
        return `${value}月`;
      }
      return value;
    },
    initUserId: null,
    initUserName: null,
    bookId: '',
    initDate: '',
    remark: '',
    money: '',
    zcCategoryList: [],
		srCategoryList: [],
		categoryList: [],
    srCategoryId: -1,
    zcCategoryId: -1,
    zcCategorySelectedIndex: 0,
    srCategorySelectedIndex: 0,
    // 支出-1 收入-0
    tabActive: 1,
    id: null,
    categoryIconSr: false,
    categoryIconZc: false,
		categoryId: '',
		show: false,
    duration: 400,
    position: 'bottom',
    round: true,
    overlay: true,
    // customStyle: 'height: 60%;',
    overlayStyle: 'background-color: rgba(0, 0, 0, 0.7)',
  },


  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    var thiz = this;
    let param = null
    const eventChannel = this.getOpenerEventChannel()
    eventChannel.on('acceptDataFromOpenerPage', function (data) {
      // 编辑时初始化参数
      param = data.dataOne
			var tab = 1;
			console.log(param.categoryId)

      if(param.type == 0){
        tab = 0
      }
      thiz.setData({
        tabActive: param.type,
        money: param.money != null && param.money != '' ? param.money : thiz.data.money,
        initData: param,
        initUserInfo: {
          userData: thiz.data.userData,
          initUserId: param.userId,
          initUserName: param.userName
        },
        categoryId: param.categoryId,
        tabActive: tab
      })
      // 查询富文本内容
      getRequest('/api/money/my/money/detailDesc?moneyId=' + param.id).then(res => {
        if (!thiz.data.detailDesc) {
          thiz.setData({
            detailDesc: res.data
          })
        }
      })
    })
    thiz.init(options)
  },
  // 初始化工作，查询分类等
  init(options) {
    console.log("执行初始化：", options)

    var cdate = this.data.initData.bookTime
      // 初始化时间
      if (cdate == null) {
        cdate = new Date();
      } else {
        cdate = new Date(cdate);
      }
      let cday = cdate.getDate();
    this.setData({
      day: cday,
      bookId: options.bookId
    })
    let id = options.id;
    if (id != undefined && id != null) {
      this.data.id = id;
    }
    this.getCategory();
    this.bookUser();
  },
  numChange: function (e) {
    this.setData({
      money: e.detail.money
    })
  },
  bookUser: function () {
    getRequest("/api/book/user/get", {
      bookId: this.data.bookId
    }).then(res => {
      // 构造用户列表hash表
      res.data.bookUserList.forEach(user => {
        user.userName = user.userName != '' && user.userName != null ? user.userName : user.reallyName;
      })
      this.setData({
        userData: [{
          id: -1,
          userName: '自定义'
        }, ...res.data.bookUserList]
      })
    })
  },
  submit: function (e) {
    if (!c.checkStrNotNull(this.data.money)) {
      wx.showToast({
        title: "请输入金额",
        icon: 'error'
      });
      return;
    }
    if(this.data.tabActive == 1 && this.data.zcCategoryId < 0){
      wx.showToast({
        title: "请输入支出分类",
        icon: 'error'
      });
      return;
    }

    if(this.data.tabActive != 1 && this.data.srCategoryId < 0){
      wx.showToast({
        title: "请输入收入分类",
        icon: 'error'
      });
      return;
    }

    if (e.detail.remark != null && e.detail.remark.length > 25) {
      wx.showToast({
        title: "备注超过25个字",
        icon: 'error'
      });
      return;
    }
    let categoryItem = this.data.tabActive == 1 ?
      this.data.zcCategoryList[this.data.zcCategoryId] :
      this.data.srCategoryList[this.data.srCategoryId];
    // 微信组件editor每次编辑详情之后，清空里面的值，就会留下字符串"<p><br></p>" 所以判断如果等于“<p><br></p>”，直接清空为空串
    let detailDesc = this.data.detailDesc == "<p><br></p>" ? "" : this.data.detailDesc
    let params = {
      ...e.detail,
      money: parseFloat(e.detail.money) * 100,
      type: this.data.tabActive,
      bookId: this.data.bookId, //账单id
      bookTime: e.detail.date,
      categoryId: categoryItem.id,
      categoryName: categoryItem.name,
      remark: e.detail.remark,
      detailDesc,
    }
    var flag = true;
    let url = '/api/money/save';
    if (params.id != null) {
      url = '/api/money/update'
      flag = false;

    }
    postRequest(url, params).then(res => {
      if (res.status == 0) {
        this.setData({
          detailDesc: ""
        })
        var title = flag ? '成功记一笔' : '修改成功'
        wx.showToast({
          title,
          icon: 'success'
        })
        // 如果是修改，回到主页
        // if (!flag)wx.switchTab({
        //   url: '/pages/index/index'
        // })
        wx.setStorage({
          key: 'hasBook',
          data: 1
        })
        wx.switchTab({
            url: '/pages/index/index'
        })
      } else {
        wx.showToast({
          title: res.msg,
          icon: 'error'
        })
      }

    })
  },
  //获取分类数据
  getCategory() {
    let thiz = this;
    getRequest('/api/category/findBookIdList').then(res => {
      
      thiz.setData({
        srCategoryList: res.data.srCategory,
        zcCategoryList: res.data.zcCategory
      })
      // 寻找分类
      thiz.setCatIndex();
    })
  },
  setCatIndex() {
          //  默认收入支出类型
    var tabActive = this.data.tabActive
    var zccategoryKey = 'srCategoryId'
    var categoryIndex = -1
    //默认分类
    if (tabActive == 1) {
      //  分类类型（0-收入 1-支出）
      zccategoryKey = 'zcCategoryId'
      for (let i = 0; i < this.data.zcCategoryList.length; i++) {
        if (this.data.zcCategoryList[i].id == this.data.categoryId) {
          categoryIndex = i
          break;
        }
      }
    } else {
      for (let i = 0; i < this.data.srCategoryList.length; i++) {
        if (this.data.srCategoryList[i].id == this.data.categoryId) {
          categoryIndex = i
          break;
        }
      }
    }
    var data = {}
    data[zccategoryKey] = categoryIndex
    this.setData(data)
  },
  onViewCategoryIcon(e) {
    this.setData({
      categoryIconZc: false,
      categoryIconSr: false,
    })
  },
  onViewCategoryIconZc(e) {
    this.setData({
      categoryIconZc: !this.data.categoryIconZc
    })
  },
  onViewCategoryIconSr(e) {
    this.setData({
      categoryIconSr: !this.data.categoryIconSr
    })
  },
  onRemoveCategory(e) {
    // 分类id
    let categoryId = e.currentTarget.dataset.id;
    getRequest("/api/category/remove", {
      categoryId
    }).then(res => {
      if (res.status == 0) {
        // 支出-1 收入-0
        if (this.data.tabActive == 1) {
          for (let i = this.data.zcCategoryList.length - 1; i >= 0; i--) {
            // 前端做支出分类逻辑删除
            if (this.data.zcCategoryList[i].id == categoryId) {
              this.data.zcCategoryList.splice(i, 1)
              this.setData({
                zcCategoryList: this.data.zcCategoryList
              })
              return;
            }
          }
        } else if (this.data.tabActive == 0) {
          for (let i = this.data.srCategoryList.length - 1; i >= 0; i--) {
            // 前端做收入分类逻辑删除
            if (this.data.srCategoryList[i].id == categoryId) {
              this.data.srCategoryList.splice(i, 1)
              this.setData({
                srCategoryList: this.data.srCategoryList
              })
              return;
            }
          }
        }
      } else {
        wx.showToast({
          title: res.msg,
          icon: 'none'
        });
      }
    }).catch(err => {
      wx.showToast({
        title: "删除失败",
        icon: 'error'
      });
    })
  },
  //分类被选中
  categroyItemSelect(e) {
    let index = e.currentTarget.dataset.index;
    if (this.data.tabActive === 1) {
      this.setData({
        zcCategorySelectedIndex: index
      })
    } else {
      this.setData({
        srCategorySelectedIndex: index
      })
    }

  },
  //tabs切换
  onTabChange(e) {
    this.setData({
      tabActive: e.detail.index
    })
  },
  showDateSelect() {
    this.setData({
      show: true
    });
  },
  //关闭选择日期
  onClose() {
    this.setData({
      show: false
    });
  },
  onAddCatrgory(e) {
    let type = e.currentTarget.dataset.index;
    let bookId = this.data.bookId
    wx.navigateTo({
      url: '/pages/category/category?type=' + type + '&bookId=' + bookId,
      success: function (res) {
        // 通过eventChannel向被打开页面传送数据
        // res.eventChannel.emit('acceptDataFromOpenerPage', {
        //   type,
        //   bookId
        // })
      }
    })
  },
  // 记一笔的详情
  onMoneyDetail(e) {
    const thiz = this
    wx.navigateTo({
      url: '/pages/mark/editor/editor',
      success: function (res) {
        // 通过eventChannel向被打开页面传送数据
        res.eventChannel.emit('acceptDataFromOpenerPage', {
          data: thiz.data.detailDesc,
          businessId: thiz.data.initData.id
        })
      }
    })

  },
//   bindPickerChange: function (e) {
//     this.setData({
//         'srCategoryId': e.detail.value
//     })
// },
// bindPickerChangeZc: function (e) {
//   this.setData({
//       'zcCategoryId': e.detail.value
//   })
// },

  onShareAppMessage() {
    return {
      title: '记账本，随心所记',
      path: '/pages/index/index',
    }
	},
	choseCategory(){
		let categoryList = '';
		if(this.data.tabActive == 1){
			categoryList = this.data.zcCategoryList
		}else{
			categoryList = this.data.srCategoryList
		}
		this.setData({
			show: true,
			categoryList: categoryList
		})
	},
	categoryChange(e){
		let index = e.currentTarget.dataset.value
		if (this.data.tabActive == 1){
				this.setData({
					zcCategoryId: index,
					show: false
				})
		}else{
			this.setData({
				srCategoryId: index,
				show: false
			})
		}
		
	},
	
})