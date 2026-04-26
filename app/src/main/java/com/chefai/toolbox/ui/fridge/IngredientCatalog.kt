package com.chefai.toolbox.ui.fridge

enum class IngredientCategory(val label: String, val emoji: String) {
    VEGETABLE("蔬菜", "🥬"),
    MEAT("肉類", "🥩"),
    SEAFOOD("海鮮", "🦐"),
    EGG_DAIRY_BEAN("蛋奶豆", "🥚"),
    GRAIN("五穀雜糧", "🌾"),
    SEASONING("調味料", "🧂"),
    OTHER("其他", "✏️"),
}

data class IngredientGroup(
    val category: IngredientCategory,
    val items: List<String>,
    val commonCount: Int = COMMON_COUNT,
) {
    val commonItems: List<String> get() = items.take(commonCount)
    val extraItems: List<String> get() = items.drop(commonCount)
    val hasExtras: Boolean get() = items.size > commonCount

    companion object {
        const val COMMON_COUNT = 12
    }
}

object IngredientCatalog {

    val groups: List<IngredientGroup> = listOf(
        IngredientGroup(
            IngredientCategory.VEGETABLE,
            listOf(
                // 常用 12
                "高麗菜", "大白菜", "青江菜", "空心菜", "菠菜", "洋蔥",
                "青蔥", "大蒜", "薑", "紅蘿蔔", "馬鈴薯", "番茄",
                // 展開 18
                "小白菜", "A菜", "地瓜葉", "芥蘭", "青椒", "紅椒",
                "小黃瓜", "花椰菜", "青花菜", "芹菜", "九層塔", "香菜",
                "韭菜", "玉米", "玉米筍", "金針菇", "香菇", "杏鮑菇",
            ),
        ),
        IngredientGroup(
            IngredientCategory.MEAT,
            listOf(
                // 常用 12
                "梅花豬", "五花肉", "里肌肉", "豬絞肉", "排骨", "雞胸肉",
                "雞腿肉", "雞翅", "牛肉片", "牛絞肉", "培根", "香腸",
                // 展開 18
                "火腿", "豬肝", "豬肉絲", "豬肉片", "棒棒腿", "雞胗",
                "雞肝", "牛小排", "牛腩", "牛肋條", "羊肉片", "鴨胸",
                "鴨肉", "熱狗", "肉鬆", "燻雞肉", "貢丸", "火鍋肉片",
            ),
        ),
        IngredientGroup(
            IngredientCategory.SEAFOOD,
            listOf(
                // 常用 12
                "蝦仁", "白蝦", "草蝦", "鮭魚", "鯛魚", "鱈魚",
                "蛤蜊", "花枝", "透抽", "小卷", "魚肉片", "鯖魚",
                // 展開 18
                "鮪魚", "虱目魚", "秋刀魚", "午仔魚", "石斑魚", "章魚",
                "鎖管", "牡蠣", "扇貝", "干貝", "海帶", "海帶芽",
                "魚板", "魚丸", "蟹肉棒", "柳葉魚", "吻仔魚", "櫻花蝦",
            ),
        ),
        IngredientGroup(
            IngredientCategory.EGG_DAIRY_BEAN,
            listOf(
                // 常用 12
                "雞蛋", "鴨蛋", "鹹蛋", "皮蛋", "牛奶", "起司片",
                "優格", "奶油", "板豆腐", "嫩豆腐", "豆干", "油豆腐",
                // 展開 18
                "鵪鶉蛋", "鮮奶油", "馬札瑞拉起司", "帕瑪森起司", "奶油起司", "煉乳",
                "酸奶", "燕麥奶", "豆漿", "豆皮", "豆腐皮", "凍豆腐",
                "毛豆", "黃豆", "黑豆", "紅豆", "綠豆", "納豆",
            ),
        ),
        IngredientGroup(
            IngredientCategory.GRAIN,
            listOf(
                // 常用 12
                "白米", "糙米", "麵條", "義大利麵", "烏龍麵", "米粉",
                "冬粉", "吐司", "水餃皮", "地瓜", "麵粉", "太白粉",
                // 展開 18
                "玉米粉", "地瓜粉", "糯米", "紫米", "燕麥", "藜麥",
                "雜糧麵包", "貝果", "刀削麵", "拉麵", "雞蛋麵", "餛飩皮",
                "年糕", "蘿蔔糕", "芋頭", "南瓜", "栗子", "薏仁",
            ),
        ),
        IngredientGroup(
            IngredientCategory.SEASONING,
            listOf(
                // 常用 12
                "醬油", "鹽", "糖", "白胡椒", "黑胡椒", "麻油",
                "米酒", "烏醋", "白醋", "蠔油", "番茄醬", "豆瓣醬",
                // 展開 18
                "香油", "蒜粉", "辣椒粉", "沙茶醬", "味噌", "辣油",
                "雞粉", "五香粉", "八角", "花椒", "蜂蜜", "橄欖油",
                "芥末", "咖哩粉", "孜然粉", "魚露", "紹興酒", "冰糖",
            ),
        ),
    )
}
