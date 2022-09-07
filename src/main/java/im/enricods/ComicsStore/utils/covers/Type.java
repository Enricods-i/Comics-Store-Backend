package im.enricods.ComicsStore.utils.covers;

public enum Type {
    COLLECTION("coll_"),
    COMIC("cmc_"),
    AUTHOR("auth_");

    private final String label;

    private Type(String label){
        this.label = label;
    }

    public String getLabel(){
        return label;
    }

}//Type
