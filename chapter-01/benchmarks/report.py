#!/usr/bin/env python3
"""Print the allocation reduction from a JMH JSON result file."""

import argparse
import json
import math
from pathlib import Path


METHODS = {
    "createFivePerRequest": "요청마다 5개 생성",
    "reuseFiveSingletonBeans": "싱글톤 5개 재사용",
}


def allocation_scores(path: Path) -> dict[str, float]:
    results = json.loads(path.read_text(encoding="utf-8"))
    scores = {}
    for result in results:
        method = result["benchmark"].rsplit(".", 1)[-1]
        if method not in METHODS:
            continue
        metric = result["secondaryMetrics"]["gc.alloc.rate.norm"]
        if metric["scoreUnit"] != "B/op":
            raise ValueError(f"{method}: 예상하지 못한 단위 {metric['scoreUnit']}")
        scores[method] = float(metric["score"])

    if set(scores) != set(METHODS):
        raise ValueError("두 벤치마크 결과가 모두 필요합니다. JMH에 -prof gc를 지정했는지 확인하세요.")
    if any(not math.isfinite(score) or score < 0 for score in scores.values()):
        raise ValueError("할당량 측정값이 유효하지 않습니다.")
    if scores["createFivePerRequest"] == 0:
        raise ValueError("기준 경로의 할당량이 0이라 감소율을 계산할 수 없습니다.")
    return scores


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("result", type=Path, help="JMH -rf json -rff 결과 파일")
    parser.add_argument("--rps", type=float, default=500, help="환산에 사용할 작업 수/초 (기본값: 500)")
    args = parser.parse_args()
    if not math.isfinite(args.rps) or args.rps <= 0:
        parser.error("--rps는 0보다 큰 수여야 합니다.")

    scores = allocation_scores(args.result)
    created = scores["createFivePerRequest"]
    reused = scores["reuseFiveSingletonBeans"]
    reduction = (created - reused) / created * 100

    print("JMH 객체 할당 비교 (1 op = 객체 5개를 사용하는 작업 1회)")
    for method, label in METHODS.items():
        score = scores[method]
        print(f"{label}: {score:,.6f} B/op; {args.rps:g} 작업/초 환산 {score * args.rps / 1024:,.4f} KiB/s")
    print(f"객체 할당량 감소율: {reduction:.4f}%")
    print("감소율 = (새로 생성한 경로의 B/op - 재사용 경로의 B/op) / 새로 생성한 경로의 B/op × 100")
    print("주의: 요청률은 측정한 서버 부하가 아니라 B/op 환산에만 사용했습니다.")


if __name__ == "__main__":
    main()
