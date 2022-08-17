package im.enricods.ComicsStore.utils.Covers;

public enum Type {
    COLLECTION("coll_"),
    COMIC("comc_"),
    AUTHOR("auth_");

    private final String label;

    private Type(String label){
        this.label = label;
    }

    public String getLabel(){
        return label;
    }

}//Type
