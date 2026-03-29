import type { HFModel } from "../types/huggingface";

function formatNumber(n: number): string {
  if (n >= 1_000_000_000) return `${(n / 1_000_000_000).toFixed(1)}B`;
  if (n >= 1_000_000) return `${(n / 1_000_000).toFixed(1)}M`;
  if (n >= 1_000) return `${(n / 1_000).toFixed(1)}K`;
  return `${n}`;
}

interface HFStatsBarProps {
  allModels: HFModel[];
  filteredCount: number;
  totalDownloads: number;
  page: number;
  totalPages: number;
}

export function HFStatsBar({ allModels, filteredCount, totalDownloads, page, totalPages }: HFStatsBarProps) {
  const stats = [
    { label: "Total Models", value: formatNumber(allModels.length) },
    { label: "Filtered", value: formatNumber(filteredCount) },
    { label: "Page Downloads", value: formatNumber(totalDownloads) },
    { label: "Page", value: `${page + 1} / ${totalPages || 1}` },
  ];

  return (
    <div className="flex bg-zinc-800 border-b border-zinc-700">
      {stats.map((s) => (
        <div key={s.label} className="flex-1 flex flex-col items-center py-3 bg-zinc-900">
          <span className="text-xl font-bold text-yellow-400">{s.value}</span>
          <span className="text-xs text-zinc-500 mt-0.5">{s.label}</span>
        </div>
      ))}
    </div>
  );
}

export { formatNumber };
