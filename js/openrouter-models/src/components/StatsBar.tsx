import type { Model, ProviderGroup } from "../types/model";

function formatPrice(price: string): string {
  const p = parseFloat(price);
  if (p === 0) return "Free";
  if (p < 0.00001) return `$${(p * 1_000_000).toFixed(3)}/M`;
  if (p < 0.001) return `$${(p * 1_000).toFixed(3)}/K`;
  return `$${p.toFixed(6)}`;
}

function formatContext(length: number): string {
  if (length >= 1_000_000) return `${(length / 1_000_000).toFixed(1)}M`;
  if (length >= 1_000) return `${(length / 1_000).toFixed(0)}K`;
  return `${length}`;
}

function formatDate(timestamp: number): string {
  return new Date(timestamp * 1000).toLocaleDateString("zh-CN");
}

interface StatsBarProps {
  models: Model[];
  providers: ProviderGroup[];
}

export function StatsBar({ models, providers }: StatsBarProps) {
  const totalModels = models.length;
  const totalProviders = providers.length;
  const freeModels = models.filter(
    (m) => parseFloat(m.pricing.prompt) === 0 && parseFloat(m.pricing.completion) === 0
  ).length;
  const avgContext =
    models.reduce((sum, m) => sum + m.context_length, 0) / totalModels;

  const stats = [
    { label: "Total Models", value: totalModels },
    { label: "Providers", value: totalProviders },
    { label: "Free Models", value: freeModels },
    { label: "Avg Context", value: formatContext(Math.round(avgContext)) },
  ];

  return (
    <div className="flex bg-zinc-800 border-b border-zinc-700">
      {stats.map((s) => (
        <div key={s.label} className="flex-1 flex flex-col items-center py-3 bg-zinc-900">
          <span className="text-xl font-bold text-indigo-400">{s.value}</span>
          <span className="text-xs text-zinc-500 mt-0.5">{s.label}</span>
        </div>
      ))}
    </div>
  );
}

export { formatPrice, formatContext, formatDate };
