package br.edu.lampi.infrareport.specifications;

import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.data.jpa.domain.Specification;

import br.edu.lampi.infrareport.model.call.Call;
import br.edu.lampi.infrareport.model.call.CallPriority;
import jakarta.persistence.criteria.Predicate;

public class CallSpecification {
    public static Specification<Call> callsBetween(LocalDateTime initDateTime, LocalDateTime endDateTime) {
        return (root, query, criteriaBuilder) -> {
            if (initDateTime == null && endDateTime == null) {
                return null;
            }
            Predicate predicate = criteriaBuilder.conjunction();
            if (initDateTime != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(root.get("dateTime"), initDateTime));
            }
            if (endDateTime != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThanOrEqualTo(root.get("dateTime"), endDateTime));
            }
            return predicate;
        };
}

    public static Specification<Call> callHasTeam(Set<Long> teamIdList) {

        if (teamIdList == null || teamIdList.isEmpty()) {
            return null;
        }

        return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.in(root.get("team").get("id")).value(teamIdList);
    }

    public static Specification<Call> callHasCategory(Set<Long> categoryIdList) {

        if (categoryIdList == null || categoryIdList.isEmpty()) {
            return null;
        }

        return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.in(root.get("category").get("id")).value(categoryIdList);
    }

    public static Specification<Call> callHasCallStatus(Set<Long> callStatusIdList) {

        if (callStatusIdList == null || callStatusIdList.isEmpty()) {
            return null;
        }

        return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.in(root.get("callStatus").get("id")).value(callStatusIdList);
    }

    public static Specification<Call> callHasFloor(Set<Long> floorIdList) {
        if (floorIdList == null || floorIdList.isEmpty()) {
            return null;
        }

        return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.in(root.get("floor").get("id")).value(floorIdList);
    }

    public static Specification<Call> callHasCallPriority(Set<CallPriority> callPriorityList) {

        if (callPriorityList == null || callPriorityList.isEmpty()) {
            return null;
        }

        return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.in(root.get("priority")).value(callPriorityList);
    }

    public static Specification<Call> callHasActive(Boolean active) {

        if (active == null) {
            return null;
        }

        return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.equal(root.get("active"), active);
    }

    public static Specification<Call> callHasUser(Set<Long> userIdList) {

        if (userIdList == null || userIdList.isEmpty()) {
            return null;
        }

        return (root, criteriaQuery, criteriaBuilder) ->
        criteriaBuilder.in(root.get("user").get("id")).value(userIdList);
    }

    public static Specification<Call> isCallClassified(Boolean classified) {
        if (classified == null) {
            return null;
        }

        if(classified){
            return (root, criteriaQuery, criteriaBuilder) ->
                criteriaBuilder.and(criteriaBuilder.isNotNull(root.get("team")),
                criteriaBuilder.isNotNull(root.get("category")),
                criteriaBuilder.isNotNull(root.get("callStatus")),
                criteriaBuilder.isNotNull(root.get("priority"))
            );
        }else{
            return (root, criteriaQuery, criteriaBuilder) ->
            criteriaBuilder.or(criteriaBuilder.isNull(root.get("team")),
                criteriaBuilder.isNull(root.get("category")),
                criteriaBuilder.isNull(root.get("callStatus")),
                criteriaBuilder.isNull(root.get("priority"))
            );
        }
    }
}
