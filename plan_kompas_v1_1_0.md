# Plan Rozwoju Aplikacji Kompas (v1.1.0)

## 1. Zrozumienie Wizji i Założeń

Celem jest przekształcenie Kompasu w profesjonalny, taktyczny instrument nawigacyjny o zerowym narzucie pamięciowym (waga APK poniżej 1 MB), z pełnym poszanowaniem ekranów OLED (fizycznie wygaszone piksele tła) oraz elastyczną konfiguracją dla użytkownika.

```
┌─────────────────────────────────────────────────────────────────┐
│                     KOMPAS - NOWE FUNKCJE                      │
├────────────────────────────────┬────────────────────────────────┤
│       MENU: PODPOWIEDZI        │         MENU: DŻUNGLA          │
│  [x] WŁĄCZONE:                 │  [x] WŁĄCZONE (Stealth OLED):  │
│      - Podwójny okrąg tarczy   │      - Tło: #000000 (OLED OFF) │
│      - Kąty: 0°, 90°, 180°...  │      - Igła: zielony mat       │
│      - Nazwa: WSCHÓD (w lang)  │      - Cyfry: szary mat        │
│  [ ] WYŁĄCZONE (v1.0.0 Pure):  │      - Zero odblasków w lesie  │
│      - Sama igła + N, E, S, W  │  [ ] WYŁĄCZONE (Classic):      │
│      - Czysty kąt numeryczny   │      - Czerwony + biały        │
└────────────────────────────────┴────────────────────────────────┘
```

---

## 2. Kluczowe Elementy Planu

### A. Nowe Menu Konfiguracyjne (Settings Sheet)
* **Dostęp:** Dyskretna, minimalistyczna ikonka zębatki `⚙` lub suwaków w rogu ekranu (niewpływająca na odczyt igły).
* **Forma:** Elegancki, ciemny panel wysuwany od dołu (*Modal Bottom Sheet*) z dwoma czytelnymi przełącznikami:
  1. **Podpowiedzi i podziałka** (Domyślnie: Włączone)
  2. **Tryb Dżungla / Nocny kamuflaż** (Domyślnie: Wyłączone)
* **Zapis stanu:** Lekki `DataStore` lub `SharedPreferences` – kompas pamięta wybór użytkownika po ponownym otwarciu.

---

### B. Przełącznik 1: „Podpowiedzi” (Tarcza Rozszerzona vs Minimalistyczna)
* **Gdy zaznaczone [ON] (Tryb bogaty):**
  * Wyświetla się podwójny okrąg tarczy.
  * Na obwodzie pojawiają się stopnie kątowe: `0°` (lub `360°`), `90°`, `180°`, `270°` (oraz subtelne kreseczki co 30°/45°).
  * Wyświetla się pełna nazwa kierunku (np. `101° E • WSCHÓD`).
* **Gdy odznaczone [OFF] (Tryb surowy / v1.0.0 Pure):**
  * Znika podwójny okrąg i cyfry pomocnicze na tarczy.
  * Zostaje wyłącznie:
    1. Dynamiczna, precyzyjna igła 3D.
    2. Główne litery kardynalne (`N`, `E`, `S`, `W`).
    3. Główny kąt numeryczny (np. `101° E`).
  * Maksymalna prostota – czysty instrument wojskowo-lotniczy.

---

### C. Przełącznik 2: Tryb „Dżungla” (Stealth Tactical Mode & Pure OLED)
Zaprojektowany specjalnie do lasu, nocnych wędrówek i sytuacji, w których telefon nie może zdradzić pozycji odblaskiem ani łuną świetlną.

| Element interfejsu | Tryb Klasyczny (Normalny) | Tryb „Dżungla” (Taktyczny Kamuflaż) |
| :--- | :--- | :--- |
| **Północna igła** | Intensywny czerwony (`#E53935`) | **Matowy militarny zielony (`#4E7043` / `#3E5C32`)** |
| **Południowa igła** | Czysty biały (`#FFFFFF`) | **Matowy grafitowy szary (`#5A6065`)** |
| **Cyfry i stopnie** | Jasny biały (`#F4F4F5`) | **Matowy szary o niskiej emisji (`#787F85`)** |
| **Litery kardynalne** | Białe z czerwoną północą | **Matowe szare z matową zieloną północą** |
| **Tło ekranu** | Czarny `#101010` | **Bezwzględny AMOLED Czarny `#000000` (LED OFF)** |

> [!IMPORTANT]
> **Fizyka OLED („Asembler w Kotlinie”):**
> Na ekranach OLED (jak w Google Pixel 9 Pro) piksel o wartości RGB `#000000` oznacza **fizyczne odcięcie zasilania diody organicznej**. 
> Ekran w 95% powierzchni nie emituje ani jednego fotonu światła. Świecą tylko pojedyncze, matowo-zielone i matowo-szare piksele samej igły i cyfr. Telefon nie daje żadnej łuny na twarz ani odblasku w koronach drzew.

---

### D. Wielojęzyczność Nazw Kierunków (System Locale)
* Zamiast ciężkich bibliotek, napisy kierunków świata pobierane są natywnie z zasobów Androida na podstawie języka systemowego telefonu użytkownika:
  * Polski: `PÓŁNOC`, `WSCHÓD`, `POŁUDNIE`, `ZACHÓD`
  * Angielski: `NORTH`, `EAST`, `SOUTH`, `WEST`
  * Niemiecki: `NORD`, `OST`, `SÜD`, `WEST`
  * Hiszpański: `NORTE`, `ESTE`, `SUR`, `OESTE`
  * Francuski, Włoski, Ukraiński itd.
* **Rozmiar:** Dodanie 4 słów w 20 najpopularniejszych językach w plikach `res/values-*/strings.xml` waży łącznie **mniej niż 3 KB**!

---

### E. Wydajność, Kod i Rozmiar Aplikacji
* **Zero zewnętrznych bibliotek graficznych:** Całość rysowana w czystym Canvas Jetpack Compose (`drawPath`, `drawCircle`, `drawText`).
* **Zero bitmap:** Żadnych plików PNG czy SVG obciążających paczkę – matematyka wektorowa.
* **Waga APK:** Zachowanie skrajnej minifikacji R8/ProGuard – APK pozostanie na poziomie **~700–800 KB**.
* **Pion i Poziom:** Nienaruszony mechanizm `BoxWithConstraints` i `remapCoordinateSystem` (tarcza z lewej, dane z prawej w poziomie).

---

## 3. Harmonogram Wdrożenia

1. **Etap testów (Teraz):**
   * Zostawiamy wersję 1.0.0 w Google Play bez zmian, żeby nie stresować znajomych i spokojnie odliczać 14 dni.
2. **Etap deweloperski (Lokalnie):**
   * Przygotowujemy i testujemy kod v1.1.0 na Pixelu 9 Pro.
3. **Etap wdrożenia produkcyjnego (Po uzyskaniu dostępu do produkcji):**
   * Wydanie v1.1.0 bezpośrednio do Sklepu Google Play jako oficjalna wielka aktualizacja.
