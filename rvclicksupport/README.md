**Recycler view click Library**

Adds the ability to click on recycle view

Source: http://www.littlerobots.nl/blog/Handle-Android-RecyclerView-Clicks/

Usage:

1 add a module to the project

2 in build.gradle write -  compile project(':rvclicksupport')

3 in code -


```Java
ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
    @Override
    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
        // do it
    }
});

ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
    @Override
    public void onItemLongClicked(RecyclerView recyclerView, int position, View v) {
        // do it
    }
});
```
[see also](https://github.com/DmitryStarkin/RecyclerViewClickSupport/blob/master/README.md)