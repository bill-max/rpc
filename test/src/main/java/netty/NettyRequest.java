package netty;

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
public class NettyRequest {
    private String interfaceName;
    private String methodName;
}
