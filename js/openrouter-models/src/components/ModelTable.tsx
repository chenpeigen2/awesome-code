import type { Model } from "../types/model";
import { formatPrice, formatContext, formatDate } from "./StatsBar";

interface ModelTableProps {
  models: Model[];
}

export function ModelTable({ models }: ModelTableProps) {
  if (models.length === 0) {
    return <div className="p-10 text-center text-zinc-500">No models found</div>;
  }

  return (
    <div className="flex-1 overflow-auto">
      <table className="w-full border-collapse text-sm">
        <thead>
          <tr className="sticky top-0 z-10">
            <th className="bg-zinc-900 px-4 py-2.5 text-left text-xs uppercase tracking-wide text-zinc-500 border-b border-zinc-700 whitespace-nowrap">
              Model
            </th>
            <th className="bg-zinc-900 px-4 py-2.5 text-left text-xs uppercase tracking-wide text-zinc-500 border-b border-zinc-700 whitespace-nowrap">
              Modality
            </th>
            <th className="bg-zinc-900 px-4 py-2.5 text-left text-xs uppercase tracking-wide text-zinc-500 border-b border-zinc-700 whitespace-nowrap">
              Context
            </th>
            <th className="bg-zinc-900 px-4 py-2.5 text-left text-xs uppercase tracking-wide text-zinc-500 border-b border-zinc-700 whitespace-nowrap">
              Max Output
            </th>
            <th className="bg-zinc-900 px-4 py-2.5 text-left text-xs uppercase tracking-wide text-zinc-500 border-b border-zinc-700 whitespace-nowrap">
              Prompt Price
            </th>
            <th className="bg-zinc-900 px-4 py-2.5 text-left text-xs uppercase tracking-wide text-zinc-500 border-b border-zinc-700 whitespace-nowrap">
              Completion Price
            </th>
            <th className="bg-zinc-900 px-4 py-2.5 text-left text-xs uppercase tracking-wide text-zinc-500 border-b border-zinc-700 whitespace-nowrap">
              Created
            </th>
          </tr>
        </thead>
        <tbody>
          {models.map((model) => (
            <tr key={model.id} className="hover:bg-zinc-800/60 transition-colors">
              <td className="px-4 py-2.5 border-b border-zinc-800 min-w-60">
                <div className="font-medium text-zinc-100">{model.name}</div>
                <div className="text-xs text-zinc-500 font-mono mt-0.5">{model.id}</div>
              </td>
              <td className="px-4 py-2.5 border-b border-zinc-800">
                <span className="text-xs px-2 py-0.5 rounded bg-indigo-500/15 text-indigo-400 whitespace-nowrap">
                  {model.architecture.modality}
                </span>
              </td>
              <td className="px-4 py-2.5 border-b border-zinc-800 text-zinc-300">
                {formatContext(model.context_length)}
              </td>
              <td className="px-4 py-2.5 border-b border-zinc-800 text-zinc-300">
                {formatContext(model.top_provider.max_completion_tokens)}
              </td>
              <td className="px-4 py-2.5 border-b border-zinc-800 font-mono text-xs text-zinc-300">
                {formatPrice(model.pricing.prompt)}
              </td>
              <td className="px-4 py-2.5 border-b border-zinc-800 font-mono text-xs text-zinc-300">
                {formatPrice(model.pricing.completion)}
              </td>
              <td className="px-4 py-2.5 border-b border-zinc-800 text-xs text-zinc-500 whitespace-nowrap">
                {formatDate(model.created)}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
