package app.rondondon.beddit.controller;

import app.rondondon.beddit.dto.request.DescRequest;
import app.rondondon.beddit.dto.response.DescResponse;
import app.rondondon.beddit.service.DescService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/desc")
@RequiredArgsConstructor
public class DescController {
    private final DescService descService;

    @GetMapping
    public ResponseEntity<DescResponse> getDesc(Long user_id){
        return ResponseEntity.ok(descService.getDesc(user_id));
    }

    @PostMapping
    public ResponseEntity<Void> setDesc(@AuthenticationPrincipal Long user_id, DescRequest descRequest){
        descService.setDesc(user_id, descRequest);
        return  ResponseEntity.ok().build();
    }
}
