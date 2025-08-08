# BrokolInn's Whitelist Plugin

Простой и гибкий плагин для управления вайтлистом с поддержкой различных типов хранилищ и автоматической системой миграции.

## Возможности

- **Множественные типы хранения**: File, SQLite, MySQL
- **Автоматическая миграция** данных при смене типа хранилища
- **Система бэкапов** перед миграцией
- **Мультиязычность** (русский/английский) с автоопределением локали
- **Гибкая система прав доступа** с возможностью отключения проверки прав
- **Настраиваемые сообщения** кика в конфигурации
- **Автодополнение команд** в табе

## Установка

1. Поместите jar файл в папку `plugins/`
2. Перезапустите сервер
3. Настройте `config.yml` под ваши нужды
4. При необходимости измените сообщения в `locales/messages_en.yml` и `locales/messages_ru.yml`

## Команды

- `/whitelist add <имя>` - добавить игрока в вайтлист
- `/whitelist remove <имя>` - удалить игрока из вайтлиста
- `/whitelist list` - показать всех игроков в вайтлисте
- `/whitelist reload` - перезагрузить конфигурацию и вайтлист
- `/whitelist migrate <откуда> <куда>` - мигрировать данные между типами хранилища

Алиас: `/bwl`

## Права доступа

- `whitelist.manage` - полный доступ ко всем командам
- `whitelist.manage.add` - добавление игроков
- `whitelist.manage.remove` - удаление игроков
- `whitelist.manage.list` - просмотр списка
- `whitelist.manage.reload` - перезагрузка
- `whitelist.manage.migrate` - миграция данных
- `whitelist.*` - все права (только для операторов)

## Конфигурация

### Основные настройки

```yaml
storage:
  type: file # file, sqlite, mysql
  last-type: none # Автоматически устанавливается

permissions:
  require-permission: true # Отключите для доступа всем игрокам

language: auto # auto, en, ru
```

### Настройка типов хранения

**File storage** (по умолчанию):
```yaml
storage:
  type: file
```

**SQLite**:
```yaml
storage:
  type: sqlite
sqlite:
  file: whitelist.db
```

**MySQL**:
```yaml
storage:
  type: mysql
mysql:
  host: localhost
  port: 3306
  database: whitelistdb
  username: root
  password: password
```

### Настройка сообщений кика

```yaml
kick-messages:
  not-whitelisted:
    title: "&c&lYou are not whitelisted!"
    subtitle: "&7Contact administration for access"
    description: |
      &c&lВы не в вайтлисте!
      &c&lYou are not whitelisted!
      
      &7Обратитесь к администрации для получения доступа.
      &7Contact the administration for access.
      
      &8Your nickname / Ваш ник: &f%player%
```

## Миграция данных

Плагин автоматически определяет изменения типа хранилища и мигрирует данные. Также доступна ручная миграция:

```
/whitelist migrate file sqlite
/whitelist migrate sqlite mysql
/whitelist migrate mysql file
```

### Система бэкапов

Перед каждой миграцией автоматически создается бэкап в папке `plugins/brokolnnsWhitelist/backups/`.

## Локализация

Файлы локализации находятся в `plugins/brokolnnsWhitelist/locales/`:
- `messages_en.yml` - английские сообщения
- `messages_ru.yml` - русские сообщения

Плагин автоматически определяет язык игрока по его локали или использует настройку из конфига.

## Изменения в версии 1.1

- ✅ Добавлена система автоматической миграции данных
- ✅ Исправлен доступ к командам (теперь учитывается настройка прав)
- ✅ Добавлены настраиваемые сообщения кика в конфиг
- ✅ Локализация вынесена в отдельную папку `locales/`
- ✅ Добавлена система бэкапов
- ✅ Улучшена система управления правами доступа
- ✅ Добавлена команда миграции данных

## Требования

- Bukkit/Spigot/Paper 1.21+
- Java 8+
- Для MySQL: соответствующий драйвер (обычно уже включён в сервер)

## Поддержка

При возникновении проблем проверьте логи сервера и убедитесь, что:
1. Конфигурация корректна
2. База данных (если используется) доступна
3. Права доступа настроены правильно
