import type { Model } from "../types/model";
import { formatPrice, formatContext, formatDate } from "./StatsBar";

interface ModelTableProps {
  models: Model[];
}

export function ModelTable({ models }: ModelTableProps) {
  if (models.length === 0) {
    return <div className="no-results">No models found</div>;
  }

  return (
    <div className="model-table-wrapper">
      <table className="model-table">
        <thead>
          <tr>
            <th>Model</th>
            <th>Modality</th>
            <th>Context</th>
            <th>Max Output</th>
            <th>Prompt Price</th>
            <th>Completion Price</th>
            <th>Created</th>
          </tr>
        </thead>
        <tbody>
          {models.map((model) => (
            <tr key={model.id}>
              <td className="model-name-cell">
                <div className="model-name">{model.name}</div>
                <div className="model-id">{model.id}</div>
              </td>
              <td>
                <span className="modality-badge">{model.architecture.modality}</span>
              </td>
              <td>{formatContext(model.context_length)}</td>
              <td>{formatContext(model.top_provider.max_completion_tokens)}</td>
              <td className="price">{formatPrice(model.pricing.prompt)}</td>
              <td className="price">{formatPrice(model.pricing.completion)}</td>
              <td className="date">{formatDate(model.created)}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
