package app.rondondon.beddit.service;

import app.rondondon.beddit.dto.request.BedCreateRequest;
import app.rondondon.beddit.dto.response.BedResponse;
import app.rondondon.beddit.dto.response.ListObjectResponse;
import app.rondondon.beddit.entity.Bed;
import app.rondondon.beddit.exception.BedException;
import app.rondondon.beddit.exception.ErrorCode;
import app.rondondon.beddit.repo.BedRepository;
import app.rondondon.beddit.repo.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BedService {
    private final BedRepository bedRepository;
    private final PostRepository postRepository;

    private static final Logger log = LoggerFactory.getLogger(BedService.class);

    @Transactional
    public void createBed(BedCreateRequest req, Long id) {
        log.debug("Creating new bed with name {}", req.name());
        bedRepository.findByName(req.name()).ifPresent(b -> {
            log.debug("Bed with name {} already exists", req.name());
            throw new BedException(ErrorCode.BED_ALREADY_EXISTS);
        });
        Bed bed = new Bed();
        bed.setName(req.name());
        bed.setDescription(req.description());
        bedRepository.save(bed);
        log.debug("Created bed with name {}", req.name());
    }

    @Transactional
    public void deleteBed(Long bedId) {
        bedRepository.deleteById(bedId);
        log.debug("Deleted bed with id {}", bedId);
    }

    public ListObjectResponse<BedResponse> getBeds(Integer pageSize, Integer pageNumber) {
        var beds = bedRepository.findAll(PageRequest.of(pageNumber, pageSize,  Sort.by(Sort.Direction.DESC, "createdAt")));
        var bedsResponse = beds.getContent().stream().map(bed ->
                new BedResponse(bed.getId(), bed.getName(), bed.getDescription(), bed.getCreatedAt()))
                .toList();
        log.debug("Got objects, returned: {}", bedsResponse.size());
        return new ListObjectResponse<>(bedsResponse, beds.hasNext());
    }
}
