# Polityka Bezpieczeństwa (Security Policy)

## Obsługiwane Wersje (Supported Versions)

Aplikacja **Kompas** (`one.lmk.kompas`) otrzymuje bieżące aktualizacje bezpieczeństwa i poprawki błędów.

| Wersja | Status wsparcia |
| :--- | :--- |
| 1.0.x / 1.1.x | :white_check_mark: Aktywnie wspierana |
| < 1.0.0 | :x: Niewspierana |

---

## Zasady Prywatności i Architektura Bezpieczeństwa

Kompas został zaprojektowany z myślą o bezwzględnej prywatności i bezpieczeństwie użytkownika:
1. **Zero uprawnień sieciowych:** Aplikacja **nie deklaruje** uprawnienia `android.permission.INTERNET`. Kod fizycznie nie posiada możliwości nawiązania jakiegokolwiek połączenia sieciowego ani wysłania danych poza urządzenie.
2. **Zero trackerów i analityki:** Brak jakichkolwiek bibliotek telemetrycznych, reklamowych czy śledzących (np. Firebase, Google Analytics, Facebook SDK).
3. **Czyste sensory lokalne:** Odczyt danych z magnetometru i akcelerometru odbywa się wyłącznie lokalnie w pamięci RAM urządzenia.
4. **Minifikacja R8 i obfuskacja:** Kod produkcyjny jest w pełni zminifikowany i oczyszczony z nieużywanych symboli przez kompilator R8.

---

## Zgłaszanie Podatności (Reporting a Vulnerability)

Jeśli odkryłeś lukę bezpieczeństwa w aplikacji lub jej zależnościach:

1. **Nie zgłaszaj jej publicznie** w zakładce Issues.
2. Wyślij poufną wiadomość e-mail na adres:  
   👉 **`dev@lmk.one`**
3. W zgłoszeniu opisz:
   - Wersję aplikacji i model telefonu (lub wersję Androida),
   - Opis podatności i kroki do jej odtworzenia (Proof of Concept),
   - Potencjalny wpływ na bezpieczeństwo.

Zobowiązujemy się do weryfikacji każdego zgłoszenia w ciągu 48 godzin oraz opublikowania poprawki w najbliższym wydaniu.
