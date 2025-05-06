package com.application.tm_application_for_tsd.utils

object InfoForCheckBox {
    val infoBox: Array<String> = arrayOf(
        "Упаковка товара в индивидуальный короб",           // Op_1_Bl_1_Sht
        "Пересчет товара",                                  // Op_2_Bl_2_Sht
        "Фасовка/сборка монотовара в короб",                // Op_3_Bl_3_Sht
        "Маркировка товара стикером",                       // Op_4_Bl_4_Sht
        "Маркировка транспортного короба",                  // Op_5_Bl_5_Sht
        "Маркировка паллета (транспортного модуля)",        // Op_6_Blis_6_10_Sht
        "Удаление стикера/маркировки с товара",             // Op_7_Pereschyot
        "Термоупаковка товара",                             // Op_9_Fasovka_Sborka
        "Разбор товара (для маркетплейсов)",                // Op_10_Markirovka_SHT
        "Спецификация ТМ (для маркетплейсов)",              // Pechat_Etiketki_s_Opisaniem
        "Подготовка транспортного паллета к отгрузке",      // Op_11_Markirovka_Prom
        "Раскомплект заказа (полный/частичный)",            // Op_13_Markirovka_Fabr
        "Не сортируемый товар",
        "Продукты",                                          // Op_16_TU_3_5
        "Опасный товар",                                     // Op_17_TU_6_8
        "Закрытая зона",                                     // Op_468_Proverka_SHK
        "Проверка штрих-кода / срока годности",             // Pechat_Etiketki_s_SHK
        "Крупногабаритный товар",                            // Krupnogabaritnyi_Tovar
        "Ювелирные изделия",                                 // Yuvelirnye_Izdelia
        "Упаковка в пакет с клеевым слоем",                  // Op_16_TU_3_5
        "Упаковка в пакет с замком Zip Lock",                // Opasnyi_Tovar
        "Упаковка в бабл - пленку",                          // Zakrytaya_Zona
        "Тип операции",
        "Сортируемый товар",
        "Вложить в упаковку печатный материал",             // Vlozhit_v_upakovku_pechatnyi_material
        "Измерение ВГХ и передача информации",              // Izmerenie_VGH_i_peredacha_informatsii
        "Индекс за срочность (коэффициент 1,5)",            // Indeks_za_srochnost_koeff_1_5
        "Прочие работы (в т.ч. устранение аномалий)",       // Prochie_raboty_vklyuchaya_ustranenie_anomalii
        "Сборка наборов (комплектов) от 2-х штук разных товаров", // Sborka_naborov_ot_2_shtuk_raznykh_tovarov
        "Упаковка товара в гофромейлер",                    // Upakovka_tovara_v_gofromeyler
        "Хранение товара",                                   // Khranenie_tovara
        "Упаковка товара в п/э пакет",                       // Yuvelirnye_Izdelia (повтор?)

    )

    val infoBoxToDB: Array<String> = arrayOf(
        "Op_1_Bl_1_Sht",                     // Упаковка товара в индивидуальный короб
        "Op_2_Bl_2_Sht",                     // Пересчет товара
        "Op_3_Bl_3_Sht",                     // Фасовка/сборка монотовара в короб
        "Op_4_Bl_4_Sht",                     // Маркировка товара стикером
        "Op_5_Bl_5_Sht",                     // Маркировка транспортного короба
        "Op_6_Blis_6_10_Sht",                // Маркировка паллета (транспортного модуля)
        "Op_7_Pereschyot",                   // Удаление стикера/маркировки с товара
        "Op_9_Fasovka_Sborka",               // Термоупаковка товара
        "Op_10_Markirovka_SHT",              // Разбор товара (для маркетплейсов)
        "Op_469_Spetsifikatsiya_TM",         // Спецификация ТМ (для маркетплейсов)
        "Op_11_Markirovka_Prom",             // Подготовка транспортного паллета к отгрузке
        "Op_13_Markirovka_Fabr",             // Раскомплект заказа (полный/частичный)
        "Ne_Sortiruemyi_Tovar",              // Не сортируемый товар
        "Produkty",                          // Продукты
        "Opasnyi_Tovar",                     // Опасный товар
        "Zakrytaya_Zona",                    // Закрытая зона
        "Op_470_Dop_Upakovka",               // Проверка штрих-кода / срока годности
        "Krupnogabaritnyi_Tovar",            // Крупногабаритный товар
        "Yuvelirnye_Izdelia",                // Ювелирные изделия
        "Op_16_TU_3_5",                      // Упаковка в пакет с клеевым слоем
        "Op_17_TU_6_8",                      // Упаковка в пакет с замком Zip Lock
        "Op_468_Proverka_SHK",               // Упаковка в бабл - пленку
        "Upakovka_v_Gofro",
        "PriznakSortirovki",                 // Сортировка товара по признаку
        "Vlozhit_v_upakovku_pechatnyi_material", // Вложить в упаковку печатный материал
        "Izmerenie_VGH_i_peredacha_informatsii", // Измерение ВГХ и передача информации
        "Indeks_za_srochnost_koeff_1_5",     // Индекс за срочность (коэффициент 1,5)
        "Prochie_raboty_vklyuchaya_ustranenie_anomalii", // Прочие работы (в т.ч. устранение аномалий)
        "Sborka_naborov_ot_2_shtuk_raznykh_tovarov", // Сборка наборов (комплектов) от 2-х штук разных товаров
        "Upakovka_tovara_v_gofromeyler",     // Упаковка товара в гофромейлер
        "Khranenie_tovara",                  // Хранение товара
        "Upakovka_v_PE_Paket"                // Упаковка товара в п/э пакет
    )
}
