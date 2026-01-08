package searchFilters;

import com.fasterxml.jackson.databind.JsonNode;
import main.Milestone;
import users.Developer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Filters {
    public static List<FilterDev> parseDEVFiltersFromJson (JsonNode filtersNode) {
        List<FilterDev> filters = new ArrayList<>();

        if (filtersNode.has("expertiseArea")) {
            filters.add(new expertiseFilter(filtersNode.get("expertiseArea").asText()));
        }
        if (filtersNode.has("seniority")) {
            filters.add(new seniorityFilter(filtersNode.get("seniority").asText()));
        }
        // aici mai trebuie performance
        return filters;
    }

    public static List<FilterTick> parseTICKFiltersFromJson (JsonNode filtersNode, Developer dev,
        Map<String, Milestone> allMilestones) {
        List<FilterTick> filters = new ArrayList<>();

        if (filtersNode.has("businessPriority")) {
            filters.add(new BusinessPriorFilter(filtersNode.get("businessPriority").asText()));
        }
        if (filtersNode.has("type")) {
            filters.add(new TypeFilter(filtersNode.get("type").asText()));
        }
        if (filtersNode.has("createdAt")) {
            filters.add(new CreatedFilter((filtersNode.get("createdAt").asText()), 0));
        }
        if (filtersNode.has("createdBefore")) {
            filters.add(new CreatedFilter((filtersNode.get("createdBefore").asText()), -1));
        }
        if (filtersNode.has("createdAfter")) {
            filters.add(new CreatedFilter((filtersNode.get("createdAfter").asText()), 1));
        }
        if (filtersNode.has("keywords")) {
            List<String> keywords = new ArrayList<>();
            JsonNode keywordsNode = filtersNode.get("keywords");
            for (JsonNode node : keywordsNode) {
                keywords.add(node.asText());
            }
            filters.add(new KeywordFilter(keywords));
        }
        // aici mai trebuie assig
        if (filtersNode.has("availableForAssignment")) {
            filters.add(new AvailableAssigFilter(filtersNode.get("availableForAssignment").asBoolean(),
                    dev, allMilestones));
        }
        // returnez filtrele
        return filters;
    }
}
