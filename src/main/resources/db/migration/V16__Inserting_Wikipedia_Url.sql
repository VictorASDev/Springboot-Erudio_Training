UPDATE tb_person
SET wikipedia_profile_url = CASE 
    WHEN id = 2 THEN 'https://en.wikipedia.org/wiki/Ayrton_Senna'
    WHEN id = 3 THEN 'https://en.wikipedia.org/wiki/Leonardo_da_Vinci'
    WHEN id = 4 THEN 'https://en.wikipedia.org/wiki/Indira_Gandhi'
    WHEN id = 5 THEN 'https://en.wikipedia.org/wiki/Mahatma_Gandhi'
    WHEN id = 6 THEN 'https://en.wikipedia.org/wiki/Muhammad_Ali'
    WHEN id = 7 THEN 'https://en.wikipedia.org/wiki/Nelson_Mandela'
    WHEN id = 8 THEN 'https://en.wikipedia.org/wiki/Nelson_Mandela'
    WHEN id = 9 THEN 'https://en.wikipedia.org/wiki/Nikola_Tesla'

    ELSE wikipedia_profile_url
END
WHERE id <= 18;