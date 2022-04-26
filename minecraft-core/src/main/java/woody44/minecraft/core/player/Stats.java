package woody44.minecraft.core.player;

import com.google.gson.Gson;

public class Stats {

    public double Health = 100;
    public double Health_max = 100;
    public double Health_regen = 0;
    public double Health_regen_ooc = 3;

    public double Mana = 25;
    public double Mana_max = 25;
    public double Mana_regen = 0;
    public double Mana_regen_ooc = 0.5;

    public int Experience = 0, Level = 1, SkillPoints = 10, EnchantPoints;
    
    //World
    public double Karma = 1000; // Karma - Przedstawia uogólnienie stylu grania gracza, dobroć jego wyborów i postępków. wartości poniżej 500 oznaczają status Villain przez co trafia na lekko inną ścieżkę gry oraz zabicie go gwarantuje większe nagrody graczom z wysoką karmą.
    public double Karma_rec = 0.001; // Odnowa Karmy - Definiuje z jakąprędkością gracz odzyskuje punkty Karmy.
    public double Luck = 1.0; // Szczęście - ogólnie wpływa na rzuty kościami do wszelkich innych statystyk poniżej, polepsza drop-rate'y oraz pozwala wydropić lepsze rzeczy.

    //Mental
    public double Bravery = 0.0; // Odwaga - Rośnie podczas zabijania potworów, wygrywania rzutów kośćmi.
    public double Charisma = 0.0; // Charyzma - im większa tym lepsze ceny w sklepach oraz łątwiej przekonać NPC do swojego zdania; Uwarunkowana przez wybory dialogowe, 
    public double Intelligence = 0.0; // Inteligencja - po czytaniu ksiazek / czarach/ questach / dialogach
    public double Knowledge = 0.0; // wiedza ! - przedstawia poziom wiedzy na temat świata gry; Rośnie poprzez odkrywanie świata.
    public double Mood = 1.0; // nastrój ! - Przedstawia podsumowanie stanu postaci; Uwarunkowane przez jedzenie, picie, zdrowie, choroby itd.
    public double Perception = 0.0; // Percepcja - Polepsza celność Łuku, Rośnie przez książki, walkę, questy i dialogi
    public double Sanity = 1.0; // Stan Psychiczny ! - Spada gdy gracz jest w opłakanym stanie lub często zabija. Zabicie gracza z Niskim Sanity daje innym zwiększoną nagrodę. 

    //Physical
    public double Agility = 0.0; // Zwinność - Zwiększa prędkośc poruszania się.
    public double Constitution = 0.0; // Organizm /Zdrowie/ Żywotność - Zwiększa maksymalną ilość zdrowia.  
    public double Dexterity = 0.0; // Zręczność - Zwiększa prędkość dobycia broni oraz prędkość ataku, prędkośc naciągania łuku. 
    public double Hunger = 1.0; // Głód ! - Stan nakarmienia postaci. Ilość powyżej 1.0 oznacza przejedzenie przez co gracz porusza się trochę wolniej i zadaje mniejsze obrażenia; Wartość poniżej 0.25 osłabia gracza. 
    public double Parkour = 0.0; // Parkour - Zwiększa wysokość skoku, odblokowuje skille związane z parkourem.
    public double Strength = 0.0; // Siła - Zwiększa zadawane obrażenia; rośnie poprzez walkę, questy i bieganie
    public double Stealth = 0.0; // Skradanie - Zmniejsza pradopodobieństwo wykrycia; Rośnie poprzez zabójstwa od tyłu, questy, skradanie się, wraz z ARCANA pozwala używać potionów niewidzialności.
    public double Toxicity = 0.0; // Toksyczność ! - Im większa wartość tym mniejszy efekt dają pozytywne potiony; zwiększa się wraz z piciem potionów.
    public double Relentless = 0.0; // Nieustępliwość - Odpowiada za zmniejszenie efektu działania kontroli tłumu.
    
    //Activities
    public double Agriculture; // Agrokultura - Pozwala sadzić bardziej wymagające rośliny, zwiększa jakość plonów, 
    public double Arcana; // Magia - Pozwala korzystać z zaklęć, łączona z wieloma innymi umiejętnościami, zmiejsza koszty teleportacji.
    public double Archery; // Łucznictwo - Zwiększa celność podczas strzelania z łuku, Zwiększa Damage strzał, pozwala na korzystanie z większej ilośc strzał, odblokowuje skille powiązane z strzelaniem, wraz z LOGGING, ARCANA, ENGINEERING, METALLURGY, WEAPONSMITHING pozwala na odpowiednio wytwarzanie nowych broni i strzał
    public double Architectonics; // Architektonika - Pozwala korzystać z lepszych materiałów do budowania domu.
    public double Armoring; // Płatnerstwo - Pozwala tworzyć zbroje z lepszych materiałów, zwiększa jakość zbroi, wraz z ARCANA pozwala tworzyć magiczne zbroje.
    public double Brewery; // Browarnictwo - Zwiększa jakośc przyrządzanych napojów. Pozwala tworzyć trunki, wraz z ARCANA pozwala tworzyć trucizny oraz magiczne napoje.
    public double Cooking; // Gotowanie - Zwiększa jakość przyrządzanych posiłków, pozwala przygotowywać bardziej skomplikowane potrawy, wraz z ARCANA pozwala tworzyć magiczne posiłki.
    public double Engineering; // Inżynieria - Pozwala tworzyć rzeczy potrzebne do mechanizmów.
    public double Fencing; // Fechtunek - Zwiększa obrażenia od mieczy, katan, kordelasów i tym podobnych. Zwiększa szansę na sparowanie/uniknięcie ataku, pozwala korzystać z lepszego oręża. 
    public double Fishing; // Rybactwo - Zwiększa szansę na wartościowe dropy, odblokowuje nowe rodzaje wędek, pozwala łowić więcej rodzajów ryb i stworzeń
    public double Flying; // Latanie - Zwiększa prędkość lotu zwierząt oraz elytr, mniejsza fall-damage, zwiększa sterowność lotu
    public double Furnishing; // Meblowanie - Pozwala tworzyć meble wyższej jakości, posiadanie mebli daje więcej benefitów 
    public double Harvesting; // Zbieractwo - Zwiększa jakość zbieranych materiałów, Pozwala identyfikować dokładny rodzaj surowca oraz jego właściwości.
    public double Jewelcrafting; // Jubilerstwo - Pozwala tworzyć biżuterię ozdobną, Wraz z ARCANA pozwala tworzyć magiczne przedmioty.
    public double Logging; // Wycinanie lasu - Przyspiesza wycinanie drzew, daje szansę na podwojone dropy z drewna, zwiększa ilość jabłek, Tree Capitator
    public double Metallurgy; // Metalurgia / Hutnictwo - Umożliwia Tworzenie nowych materiałów z już posiadanych, zwiększa ich jakość, pozwala identyfikować rodzaj i jakość metalu
    public double Mining; // Kopanie - Pozwala Kopać materiały lepszej jakości, daje szansę na podwojenie wydobywanych surowców, zwiększa prędkość kopania,  odblokowuje skille powiązane z kopaniem,
    public double Skinning; // Skórowanie - Pozwala pozyskiwać skórę ze zwierząt, Wpływa na jej jakość oraz szybkość pozyskania 
    public double Smelting; // Wytapianie - Zwiększa ilość XP pozyskiwanego z przetapiania, pozwala przetapiać lepsze materiały oraz zwiększa prędkość przetapiania
    public double Spirit; // Duchowość - Łagodzi punkty karne Karmy za złe uczynki, delikatnie wzmacnia czary, mikstury, delikatnie wpływa na szczęście itd.  
    public double Stonecutting; // Kamieniarstwo - Pozwala tworzyć ozdoby z kamienia oraz schody, ciosane cegły itd., wraz z ARCANA zwiększa ilość many.
    public double Survival; // Przetrwanie - Odporność na choroby, zimno, szybkość głodnienia itd.
    public double Taming; // Tresowanie - Pomaga oswajać dzikie zwierzęta, łatwiejsze zacieśnianie więzów przyjaźni ze zwierzętami.
    public double Tanning; // Garbowanie - Pozwala zwiększać jakość skóry pozyskanej ze zwierząt
    public double Trading; // Handel - Modyfikuje ceny w sklepach
    public double Weaponsmithing; // Wytwarzanie broni - Wpływa na damage podstawowy broni, ilośc użyć, ostrość itd., wraz z ARCANA pozwala tworzyć magiczny oręż.
    public double Weaving; // Tkactwo - Jakość tworzonych ubrań z tkaniny (odporność na zimno, ilość użyć, wygoda itd)
    public double Woodworking; // Obróbka drewna - Możliwość, ilość i jakość tworzenia ozdób z drewna 
    public double Sailing; // Żeglarstwo - Prędkość poruszania się łodzi
    public double Swimming; // Pływanie - Prędkość pływania i poruszania się w wodzie 
    
    public String Serialize(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static Stats Build(String json){
        Gson gson = new Gson();
        return gson.fromJson(json, Stats.class);
    }
}
