package ru.multivarka;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class Messages {
    private static final Map<String, String> EN = new HashMap<>();
    private static final Map<String, String> RU = new HashMap<>();

    static {
        EN.put("usage", "Usage: java -jar mods-audit.jar [--modsDir <path>] [--curseforgeApiKey <key>] [--gameId <int>] [--jsonOutput <path>] [--verbose] [--language <lang>]");
        EN.put("usage.pause", "  --pause: wait for Enter before exit (default on Windows)");
        EN.put("usage.nopause", "  --noPause: do not wait for Enter");
        EN.put("prompt.pause", "Press Enter to exit...");
        EN.put("scan.mode", "Scan mode: non-recursive");
        EN.put("summary", "Summary:");
        EN.put("summary.total", "Total jar files: %d");
        EN.put("summary.modrinth", "Found on Modrinth: %d");
        EN.put("summary.curseforge", "Found on CurseForge: %d");
        EN.put("summary.client_required", "Client required: %d");
        EN.put("summary.client_preferred", "Client preferred: %d");
        EN.put("summary.unknown", "Others: %d");
        EN.put("client.mod", "Client mod:");
        EN.put("field.file", "  File: %s");
        EN.put("field.path", "  Path: %s");
        EN.put("field.metadata", "  Metadata: source=%s, id=%s, name=%s, version=%s");
        EN.put("field.modrinth", "  Modrinth: title=%s, slug=%s, client_side=%s, server_side=%s");
        EN.put("field.modrinth.url", "  Modrinth URL: %s");
        EN.put("field.curseforge", "  CurseForge: modId=%d, fileId=%d, name=%s, file=%s");
        EN.put("field.curseforge.url", "  CurseForge URL: %s");
        EN.put("field.reason", "  Reason: %s");
        EN.put("field.scan", "Scan: %s");
        EN.put("label.modrinth", "Modrinth: %s");
        EN.put("label.curseforge", "CurseForge: %s");
        EN.put("unmatched.details", "Unmatched details:");
        EN.put("json.saved", "JSON report saved to: %s");
        EN.put("error.scan", "Failed to scan mods directory: %s");
        EN.put("error.json", "Failed to write JSON report: %s");
        EN.put("error.missing_value", "Missing value for %s");
        EN.put("error.invalid_number", "Invalid value for %s");
        EN.put("error.unknown_arg", "Unknown argument: %s");
        EN.put("n/a", "n/a");
        EN.put("reason.modrinth.missing_sha1", "missing SHA1");
        EN.put("reason.modrinth.not_found", "not found on Modrinth");
        EN.put("reason.modrinth.rate_limited", "Modrinth rate limit");
        EN.put("reason.modrinth.http", "Modrinth HTTP %s");
        EN.put("reason.modrinth.project_http", "Modrinth project HTTP %s");
        EN.put("reason.modrinth.error", "Modrinth error: %s");
        EN.put("reason.curseforge.no_api_key", "CurseForge API key is missing");
        EN.put("reason.curseforge.no_match", "no exact match");
        EN.put("reason.curseforge.rate_limited", "CurseForge rate limit");
        EN.put("reason.curseforge.http", "CurseForge HTTP %s");
        EN.put("reason.curseforge.error", "CurseForge error: %s");
        EN.put("reason.curseforge.scan_error", "scan error: %s");
        EN.put("classification.modrinth.required_unsupported", "Modrinth: client required, server unsupported");
        EN.put("classification.modrinth.required_optional", "Modrinth: client required, server optional");
        EN.put("classification.heuristic", "Heuristic match: '%s'");
        EN.put("classification.unknown", "No client-only signal");
        EN.put("classification.heuristic.suffix", " (heuristic)");

        RU.put("usage", "Использование: java -jar mods-audit.jar [--modsDir <path>] [--curseforgeApiKey <key>] [--gameId <int>] [--jsonOutput <path>] [--verbose] [--language <lang>]");
        RU.put("usage.pause", "  --pause: ожидать Enter перед выходом (по умолчанию на Windows)");
        RU.put("usage.nopause", "  --noPause: не ждать Enter");
        RU.put("prompt.pause", "Нажмите Enter для выхода...");
        RU.put("scan.mode", "Режим сканирования: без рекурсии");
        RU.put("summary", "Сводка:");
        RU.put("summary.total", "Всего jar файлов: %d");
        RU.put("summary.modrinth", "Найдено на Modrinth: %d");
        RU.put("summary.curseforge", "Найдено на CurseForge: %d");
        RU.put("summary.client_required", "Клиент обязательный: %d");
        RU.put("summary.client_preferred", "Клиент предпочтительный: %d");
        RU.put("summary.unknown", "Остальные: %d");
        RU.put("client.mod", "Клиентский мод:");
        RU.put("field.file", "  Файл: %s");
        RU.put("field.path", "  Путь: %s");
        RU.put("field.metadata", "  Метаданные: источник=%s, id=%s, имя=%s, версия=%s");
        RU.put("field.modrinth", "  Modrinth: название=%s, slug=%s, client_side=%s, server_side=%s");
        RU.put("field.modrinth.url", "  Ссылка Modrinth: %s");
        RU.put("field.curseforge", "  CurseForge: modId=%d, fileId=%d, имя=%s, файл=%s");
        RU.put("field.curseforge.url", "  Ссылка CurseForge: %s");
        RU.put("field.reason", "  Причина: %s");
        RU.put("field.scan", "Сканирование: %s");
        RU.put("label.modrinth", "Modrinth: %s");
        RU.put("label.curseforge", "CurseForge: %s");
        RU.put("unmatched.details", "Подробности несопоставленных:");
        RU.put("json.saved", "JSON отчет сохранен: %s");
        RU.put("error.scan", "Не удалось просканировать папку модов: %s");
        RU.put("error.json", "Не удалось записать JSON отчет: %s");
        RU.put("error.missing_value", "Не указано значение для %s");
        RU.put("error.invalid_number", "Некорректное значение для %s");
        RU.put("error.unknown_arg", "Неизвестный аргумент: %s");
        RU.put("n/a", "нет данных");
        RU.put("reason.modrinth.missing_sha1", "не удалось посчитать SHA1");
        RU.put("reason.modrinth.not_found", "не найден на Modrinth");
        RU.put("reason.modrinth.rate_limited", "лимит Modrinth");
        RU.put("reason.modrinth.http", "ошибка Modrinth: HTTP %s");
        RU.put("reason.modrinth.project_http", "ошибка проекта Modrinth: HTTP %s");
        RU.put("reason.modrinth.error", "ошибка Modrinth: %s");
        RU.put("reason.curseforge.no_api_key", "API ключ CurseForge не задан");
        RU.put("reason.curseforge.no_match", "нет точного совпадения");
        RU.put("reason.curseforge.rate_limited", "лимит CurseForge");
        RU.put("reason.curseforge.http", "ошибка CurseForge: HTTP %s");
        RU.put("reason.curseforge.error", "ошибка CurseForge: %s");
        RU.put("reason.curseforge.scan_error", "ошибка сканирования: %s");
        RU.put("classification.modrinth.required_unsupported", "Modrinth: клиент обязателен, сервер не поддерживается");
        RU.put("classification.modrinth.required_optional", "Modrinth: клиент обязателен, сервер опционален");
        RU.put("classification.heuristic", "Эвристика: '%s'");
        RU.put("classification.unknown", "Нет признаков клиентского");
        RU.put("classification.heuristic.suffix", " (эвристика)");
    }

    private final Map<String, String> dictionary;

    private Messages(Map<String, String> dictionary) {
        this.dictionary = dictionary;
    }

    public static Messages forLanguage(String language) {
        if (language == null) {
            return new Messages(EN);
        }
        String normalized = language.toLowerCase(Locale.ROOT);
        if (normalized.startsWith("ru")) {
            return new Messages(RU);
        }
        return new Messages(EN);
    }

    public String msg(String key, Object... args) {
        String template = dictionary.getOrDefault(key, key);
        return String.format(template, args);
    }

    public String reason(String code) {
        if (StringUtil.isBlank(code)) {
            return msg("n/a");
        }
        String reason = code;
        String detail = null;
        int idx = code.indexOf(':');
        if (idx > 0) {
            reason = code.substring(0, idx);
            detail = code.substring(idx + 1);
        }
        String key = "reason." + reason;
        String template = dictionary.getOrDefault(key, code);
        if (detail != null && template.contains("%s")) {
            return String.format(template, detail);
        }
        return template;
    }

    public String classificationReason(String code, boolean heuristic) {
        if (StringUtil.isBlank(code)) {
            return msg("classification.unknown");
        }
        String base = code;
        String detail = null;
        int idx = code.indexOf(':');
        if (idx > 0) {
            base = code.substring(0, idx);
            detail = code.substring(idx + 1);
        }
        String template = dictionary.getOrDefault(base, code);
        String result = detail != null && template.contains("%s")
                ? String.format(template, detail)
                : template;
        if (heuristic) {
            result += dictionary.getOrDefault("classification.heuristic.suffix", " (heuristic)");
        }
        return result;
    }
}
