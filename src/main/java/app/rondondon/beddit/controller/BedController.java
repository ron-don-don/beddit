package app.rondondon.beddit.controller;


import app.rondondon.beddit.dto.request.BedCreateRequest;
import app.rondondon.beddit.dto.response.BedResponse;
import app.rondondon.beddit.dto.response.ListObjectResponse;
import app.rondondon.beddit.service.BedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/beds")
@RequiredArgsConstructor
public class BedController {
    private final BedService bedService;


    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> createBed(BedCreateRequest req, @AuthenticationPrincipal Long id){
        bedService.createBed(req, id);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<ListObjectResponse<BedResponse>> getBeds(@RequestParam(name = "page_number") Integer pageNumber, @RequestParam(name = "page_size") Integer pageSize) {
        return ResponseEntity.ok(bedService.getBeds(pageNumber, pageSize));
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBed(@RequestParam(name = "bed_id") Long bedId){
        bedService.deleteBed(bedId);
        return ResponseEntity.noContent().build();
    }
}
