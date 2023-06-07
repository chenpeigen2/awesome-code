package featuretest.init;

public class StyleListViewModel {
    String mCateListJson;

    String mInternationalCateListJson;

    public StyleListViewModel() {
        new StyleListViewModel_JsonBinding(this);
    }

    public StyleListViewModel(String mCateListJson) {
        this.mCateListJson = mCateListJson;
    }

    public static void main(String[] args) {
        var aa = new StyleListViewModel();

        System.out.println(aa);
    }
}
