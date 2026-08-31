package app.rondondon.beddit.service;

import app.rondondon.beddit.dto.request.DescRequest;
import app.rondondon.beddit.dto.response.DescResponse;
import app.rondondon.beddit.entity.Desc;
import app.rondondon.beddit.repo.DescRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DescService {
    private final DescRepository descRepository;

    public DescResponse getDesc(Long user_id){
        return descRepository.findById(user_id).map(desc -> new DescResponse(desc.getText())).orElse(new DescResponse(""));
    }
    @Transactional
    public void setDesc(Long user_id, DescRequest descRequest){
        descRepository.findByOwnerId(user_id).forEach(desc -> desc.setText(descRequest.text()));
    }
}
