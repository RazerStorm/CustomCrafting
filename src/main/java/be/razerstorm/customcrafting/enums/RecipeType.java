package be.razerstorm.customcrafting.enums;

public enum RecipeType {
    CRAFTING,
    FURNACE;

    public static boolean typeExists(String type) {
        for(RecipeType recipeType : RecipeType.values()) {
            if(recipeType.name().equalsIgnoreCase(type)) {
                return true;
            }
        }
        return false;
    }
}
