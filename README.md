# ETSearchView

导入项目：

AS3.0以前：

compile 'com.clt5201314.baiajRepo.SearchView:searchview:1.0.0'

AS3.0以后：

implementation 'com.clt5201314.baiajRepo.SearchView:searchview:1.0.0'

如何使用:

    <com.baj.searchview.SearchView
 
        android:id="@+id/et_search"
        
        android:layout_width="match_parent"
        
        android:layout_height="wrap_content"
        
        app:sv_hint="请输入你的内容" />
        
 可配置属性：
 
    sv_text:搜索框的文字
 
    sv_hint:提示文字
 
    sv_hideImage:是否隐藏搜索图标
 
    sv_textSize:文字大小
 
 如何设置搜索内容：
 
    SearchView searchView = findViewById(R.id.et_searchView);
  
    // T传入你所需要查询的实体类
  
    List<T> datas = new ArrayList<>();
  
    searchView.setSearchWay(new SearchView.SearchWay<T>() {
                @Override
                public List getData() {
                    // 返回数据源
                    return datas;
               }

               @Override
                public boolean matchItem(T item, String s) {
                     // 如果实体类中包含搜索的字符串
                    return T.getXXX().contains(s);
                }

               @Override
                public void update(List resultList) {
                    // 显示数据
                }
            });
